package com.neobank.service;

import com.neobank.constant.ApiResponse;
import com.neobank.dto.account.AccountCreateDto;
import com.neobank.dto.account.AccountResponseDto;
import com.neobank.entity.Account;
import com.neobank.entity.User;
import com.neobank.enums.AccountStatus;
import com.neobank.enums.Messages;
import com.neobank.mapper.AccountMapper;
import com.neobank.repository.AccountRepository;
import com.neobank.repository.UserRepository;
import com.neobank.util.IbanUtil;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountMapper accountMapper;

    public AccountService(AccountRepository accountRepository,
                          UserRepository userRepository,
                          AccountMapper accountMapper
    ) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.accountMapper = accountMapper;
    }

    public ApiResponse<AccountResponseDto> create(Long userId,
                                                  AccountCreateDto dto
    ) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new RuntimeException(Messages.NOT_FOUND.name()));
        String iban = IbanUtil.generate();
        while (accountRepository.existsByIban(iban)) {
            iban = IbanUtil.generate();
        }
        Account account = new Account();
        account.setUser(user);
        account.setIban(iban);
        account.setAccountType(dto.getAccountType());
        account.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : "AZN");
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);
        return new ApiResponse<>(true, accountMapper.toResponse(account), Messages.CREATED.name());
    }

    public ApiResponse<List<AccountResponseDto>> getMyAccounts(Long userId) {
        List<AccountResponseDto> list = accountRepository
                .findByUserIdAndDeletedFalse(userId)
                .stream()
                .map(accountMapper::toResponse)
                .collect(Collectors.toList());
        return new ApiResponse<>(true, list, Messages.SUCCESS.name());
    }

    public ApiResponse<AccountResponseDto> getById(Long id, Long userId) {
        Account account = accountRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException(Messages.NOT_FOUND.name()));
        if (!account.getUser().getId().equals(userId))
            throw new RuntimeException(Messages.FORBIDDEN.name());
        return new ApiResponse<>(true, accountMapper.toResponse(account), Messages.SUCCESS.name());
    }

    public ApiResponse<Void> block(Long id) {
        Account account = accountRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException(Messages.NOT_FOUND.name()));
        account.setStatus(AccountStatus.BLOCKED);
        accountRepository.save(account);
        return new ApiResponse<>(true, Messages.UPDATED.name());
    }
}