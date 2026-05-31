package com.neobank.service;

import com.neobank.constant.ApiResponse;
import com.neobank.dto.transaction.TransactionResponseDto;
import com.neobank.dto.transaction.TransferRequestDto;
import com.neobank.entity.Account;
import com.neobank.entity.Transaction;
import com.neobank.enums.AccountStatus;
import com.neobank.enums.Messages;
import com.neobank.enums.TransactionType;
import com.neobank.mapper.TransactionMapper;
import com.neobank.repository.AccountRepository;
import com.neobank.repository.CardRepository;
import com.neobank.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;
    private final CardRepository cardRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              TransactionMapper transactionMapper,
                              CardRepository cardRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.transactionMapper = transactionMapper;
        this.cardRepository = cardRepository;
    }

    private Account resolveAndValidateSender(Long userId, TransferRequestDto dto) {
        Account from = accountRepository.findByIdAndDeletedFalse(dto.getFromAccountId())
                .orElseThrow(() -> new RuntimeException(Messages.NOT_FOUND.name()));
        if (!from.getUser().getId().equals(userId))
            throw new RuntimeException(Messages.FORBIDDEN.name());
        if (from.getStatus() == AccountStatus.BLOCKED)
            throw new RuntimeException(Messages.ACCOUNT_BLOCKED.name());
        if (from.getBalance().compareTo(dto.getAmount()) < 0)
            throw new RuntimeException(Messages.INSUFFICIENT_BALANCE.name());
        return from;
    }

    private Account resolveRecipient(TransferRequestDto dto) {
        if (dto.getToCardNumber() != null && !dto.getToCardNumber().isEmpty()) {
            return cardRepository.findByCardNumberAndDeletedFalse(dto.getToCardNumber())
                    .orElseThrow(() -> new RuntimeException(Messages.NOT_FOUND.name()))
                    .getAccount();
        }
        return accountRepository.findByIbanAndDeletedFalse(dto.getToIban())
                .orElseThrow(() -> new RuntimeException(Messages.NOT_FOUND.name()));
    }

    private void executeTransfer(Account from,
                                 Account to,
                                 BigDecimal amount
    ) {
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        accountRepository.save(from);
        accountRepository.save(to);
    }

    private Transaction buildTransaction(Account from,
                                         Account to,
                                         TransferRequestDto dto
    ) {
        Transaction transaction = new Transaction();
        transaction.setFromAccount(from);
        transaction.setToAccount(to);
        transaction.setAmount(dto.getAmount());
        transaction.setCurrency(from.getCurrency());
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setDescription(dto.getDescription());
        transaction.setReferenceNumber(UUID.randomUUID().toString());
        return transaction;
    }

    @Transactional
    public ApiResponse<TransactionResponseDto> transfer(Long userId,
                                                        TransferRequestDto dto
    ) {
        Account from = resolveAndValidateSender(userId, dto);
        Account to = resolveRecipient(dto);
        executeTransfer(from, to, dto.getAmount());
        Transaction transaction = buildTransaction(from, to, dto);
        transactionRepository.save(transaction);
        return new ApiResponse<>(true, transactionMapper.toResponse(transaction), Messages.SUCCESS.name());
    }

    public ApiResponse<Page<TransactionResponseDto>> getHistory(Long accountId,
                                                                Long userId,
                                                                Pageable pageable
    ) {
        Account account = accountRepository.findByIdAndDeletedFalse(accountId)
                .orElseThrow(() -> new RuntimeException(Messages.NOT_FOUND.name()));
        if (!account.getUser().getId().equals(userId))
            throw new RuntimeException(Messages.FORBIDDEN.name());
        Page<TransactionResponseDto> page = transactionRepository
                .findAllByAccountId(accountId, pageable)
                .map(transactionMapper::toResponse);
        return new ApiResponse<>(true, page, Messages.SUCCESS.name());
    }

    public ApiResponse<TransactionResponseDto> getById(Long id,
                                                       Long userId
    ) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(Messages.NOT_FOUND.name()));
        boolean isFrom = transaction.getFromAccount() != null &&
                transaction.getFromAccount().getUser().getId().equals(userId);
        boolean isTo = transaction.getToAccount() != null &&
                transaction.getToAccount().getUser().getId().equals(userId);
        if (!isFrom && !isTo)
            throw new RuntimeException(Messages.FORBIDDEN.name());
        return new ApiResponse<>(true, transactionMapper.toResponse(transaction), Messages.SUCCESS.name());
    }
}