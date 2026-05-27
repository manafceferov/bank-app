package com.neobank.service;

import com.neobank.constant.ApiResponse;
import com.neobank.dto.credit.CreditApplyDto;
import com.neobank.dto.credit.CreditResponseDto;
import com.neobank.entity.Account;
import com.neobank.entity.Credit;
import com.neobank.entity.CreditPayment;
import com.neobank.entity.User;
import com.neobank.enums.CreditStatus;
import com.neobank.enums.Messages;
import com.neobank.mapper.CreditMapper;
import com.neobank.repository.AccountRepository;
import com.neobank.repository.CreditRepository;
import com.neobank.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CreditService {

    private static final BigDecimal ANNUAL_RATE = new BigDecimal("12.00");

    private final CreditRepository creditRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final CreditMapper creditMapper;

    public CreditService(CreditRepository creditRepository,
                         AccountRepository accountRepository,
                         UserRepository userRepository,
                         CreditMapper creditMapper) {
        this.creditRepository = creditRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.creditMapper = creditMapper;
    }

    public ApiResponse<List<CreditResponseDto>> getCreditsByAccount(Long accountId, Long userId) {
        Account account = accountRepository.findByIdAndDeletedFalse(accountId)
                .orElseThrow(() -> new RuntimeException(Messages.NOT_FOUND.name()));
        if (!account.getUser().getId().equals(userId)) {
            throw new RuntimeException(Messages.FORBIDDEN.name());
        }
        List<CreditResponseDto> list = creditRepository
                .findByAccountIdAndDeletedFalse(accountId)
                .stream()
                .map(creditMapper::toResponse)
                .collect(Collectors.toList());
        return new ApiResponse<>(true, list, Messages.SUCCESS.name());
    }

    @Transactional
    public ApiResponse<CreditResponseDto> apply(Long userId, CreditApplyDto dto) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new RuntimeException(Messages.NOT_FOUND.name()));
        Account account = accountRepository.findByIdAndDeletedFalse(dto.getAccountId())
                .orElseThrow(() -> new RuntimeException(Messages.NOT_FOUND.name()));
        if (!account.getUser().getId().equals(userId))
            throw new RuntimeException(Messages.FORBIDDEN.name());

        int n = dto.getDurationMonths();
        BigDecimal monthlyRate = ANNUAL_RATE
                .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP);
        BigDecimal pow = BigDecimal.ONE.add(monthlyRate).pow(n);
        BigDecimal monthly = dto.getAmount()
                .multiply(monthlyRate)
                .multiply(pow)
                .divide(pow.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);

        Credit credit = new Credit();
        credit.setUser(user);
        credit.setAccount(account);
        credit.setAmount(dto.getAmount());
        credit.setInterestRate(ANNUAL_RATE);
        credit.setDurationMonths(n);
        credit.setMonthlyPayment(monthly);
        credit.setStartDate(LocalDate.now());
        credit.setEndDate(LocalDate.now().plusMonths(n));
        credit.setStatus(CreditStatus.PENDING);

        List<CreditPayment> schedule = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            CreditPayment payment = new CreditPayment();
            payment.setCredit(credit);
            payment.setDueDate(LocalDate.now().plusMonths(i));
            payment.setAmount(monthly);
            payment.setPaid(false);
            schedule.add(payment);
        }
        credit.setPaymentSchedule(schedule);
        creditRepository.save(credit);

        return new ApiResponse<>(true, creditMapper.toResponse(credit), Messages.CREATED.name());
    }

    @Transactional
    public ApiResponse<Void> approve(Long creditId) {
        Credit credit = creditRepository.findByIdAndDeletedFalse(creditId)
                .orElseThrow(() -> new RuntimeException(Messages.NOT_FOUND.name()));
        credit.setStatus(CreditStatus.APPROVED);

        Account account = credit.getAccount();
        account.setBalance(account.getBalance().add(credit.getAmount()));
        accountRepository.save(account);
        creditRepository.save(credit);
        return new ApiResponse<>(true, Messages.UPDATED.name());
    }

    public ApiResponse<List<CreditResponseDto>> getMyCredits(Long userId) {
        List<CreditResponseDto> list = creditRepository
                .findByUserIdAndDeletedFalse(userId)
                .stream()
                .map(creditMapper::toResponse)
                .collect(Collectors.toList());
        return new ApiResponse<>(true, list, Messages.SUCCESS.name());
    }
}