package com.neobank.service;

import com.neobank.constant.ApiResponse;
import com.neobank.dto.deposit.DepositCreateDto;
import com.neobank.dto.deposit.DepositResponseDto;
import com.neobank.entity.Account;
import com.neobank.entity.Deposit;
import com.neobank.enums.DepositStatus;
import com.neobank.enums.Messages;
import com.neobank.mapper.DepositMapper;
import com.neobank.repository.AccountRepository;
import com.neobank.repository.DepositRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepositService {

    private static final BigDecimal ANNUAL_RATE = new BigDecimal("12.00");

    private final DepositRepository depositRepository;
    private final AccountRepository accountRepository;
    private final DepositMapper depositMapper;

    public DepositService(DepositRepository depositRepository,
                          AccountRepository accountRepository,
                          DepositMapper depositMapper
    ) {
        this.depositRepository = depositRepository;
        this.accountRepository = accountRepository;
        this.depositMapper = depositMapper;
    }

    @Transactional
    public ApiResponse<DepositResponseDto> create(Long userId, DepositCreateDto dto) {
        Account account = accountRepository.findByIdAndDeletedFalse(dto.getAccountId())
                .orElseThrow(() -> new RuntimeException(Messages.NOT_FOUND.name()));
        if (!account.getUser().getId().equals(userId))
            throw new RuntimeException(Messages.FORBIDDEN.name());
        if (account.getBalance().compareTo(dto.getAmount()) < 0)
            throw new RuntimeException(Messages.INSUFFICIENT_BALANCE.name());
        account.setBalance(account.getBalance().subtract(dto.getAmount()));
        accountRepository.save(account);
        BigDecimal rate = ANNUAL_RATE.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal profit = dto.getAmount()
                .multiply(rate)
                .multiply(BigDecimal.valueOf(dto.getDurationMonths()))
                .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
        Deposit deposit = new Deposit();
        deposit.setAccount(account);
        deposit.setAmount(dto.getAmount());
        deposit.setInterestRate(ANNUAL_RATE);
        deposit.setDurationMonths(dto.getDurationMonths());
        deposit.setStartDate(LocalDate.now());
        deposit.setEndDate(LocalDate.now().plusMonths(dto.getDurationMonths()));
        deposit.setExpectedProfit(profit);
        deposit.setStatus(DepositStatus.ACTIVE);
        depositRepository.save(deposit);
        return new ApiResponse<>(true, depositMapper.toResponse(deposit), Messages.CREATED.name());
    }

    public ApiResponse<List<DepositResponseDto>> getMyDeposits(Long accountId, Long userId) {
        Account account = accountRepository.findByIdAndDeletedFalse(accountId)
                .orElseThrow(() -> new RuntimeException(Messages.NOT_FOUND.name()));
        if (!account.getUser().getId().equals(userId))
            throw new RuntimeException(Messages.FORBIDDEN.name());
        List<DepositResponseDto> list = depositRepository
                .findByAccountIdAndDeletedFalse(accountId)
                .stream()
                .map(depositMapper::toResponse)
                .collect(Collectors.toList());
        return new ApiResponse<>(true, list, Messages.SUCCESS.name());
    }

    @Transactional
    public ApiResponse<Void> close(Long depositId, Long userId) {
        Deposit deposit = depositRepository.findByIdAndDeletedFalse(depositId)
                .orElseThrow(() -> new RuntimeException(Messages.NOT_FOUND.name()));
        if (!deposit.getAccount().getUser().getId().equals(userId))
            throw new RuntimeException(Messages.FORBIDDEN.name());
        if (deposit.getStatus() != DepositStatus.ACTIVE)
            throw new RuntimeException(Messages.DEPOSIT_ALREADY_CLOSED.name());
        Account account = deposit.getAccount();
        account.setBalance(account.getBalance().add(deposit.getAmount()).add(deposit.getExpectedProfit()));
        accountRepository.save(account);
        deposit.setStatus(DepositStatus.CLOSED);
        depositRepository.save(deposit);
        return new ApiResponse<>(true, Messages.UPDATED.name());
    }
}