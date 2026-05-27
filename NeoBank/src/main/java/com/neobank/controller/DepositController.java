package com.neobank.controller;

import com.neobank.constant.ApiResponse;
import com.neobank.dto.deposit.DepositCreateDto;
import com.neobank.dto.deposit.DepositResponseDto;
import com.neobank.service.DepositService;
import com.neobank.util.SecurityUtil;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/deposits")
public class DepositController {

    private final DepositService depositService;

    public DepositController(DepositService depositService) {
        this.depositService = depositService;
    }

    @PostMapping
    public ApiResponse<DepositResponseDto> create(@RequestBody DepositCreateDto dto) {
        return depositService.create(SecurityUtil.getCurrentUserId(), dto);
    }

    @GetMapping("/account/{accountId}")
    public ApiResponse<List<DepositResponseDto>> getMyDeposits(@PathVariable Long accountId) {
        return depositService.getMyDeposits(accountId, SecurityUtil.getCurrentUserId());
    }

    @PatchMapping("/{depositId}/close")
    public ApiResponse<Void> close(@PathVariable Long depositId) {
        return depositService.close(depositId, SecurityUtil.getCurrentUserId());
    }
}