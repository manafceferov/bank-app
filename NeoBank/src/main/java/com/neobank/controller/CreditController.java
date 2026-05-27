package com.neobank.controller;

import com.neobank.constant.ApiResponse;
import com.neobank.dto.credit.CreditApplyDto;
import com.neobank.dto.credit.CreditResponseDto;
import com.neobank.service.CreditService;
import com.neobank.util.SecurityUtil;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/credits")
public class CreditController {

    private final CreditService creditService;

    public CreditController(CreditService creditService) {
        this.creditService = creditService;
    }

    @PostMapping("/apply")
    public ApiResponse<CreditResponseDto> apply(@RequestBody CreditApplyDto dto) {
        return creditService.apply(SecurityUtil.getCurrentUserId(), dto);
    }

    @PatchMapping("/{creditId}/approve")
    public ApiResponse<Void> approve(@PathVariable Long creditId) {
        return creditService.approve(creditId);
    }

    @GetMapping("/account/{accountId}")
    public ApiResponse<List<CreditResponseDto>> getCreditsByAccount(@PathVariable Long accountId) {
        return creditService.getCreditsByAccount(accountId, SecurityUtil.getCurrentUserId());
    }

    @GetMapping
    public ApiResponse<List<CreditResponseDto>> getMyCredits() {
        return creditService.getMyCredits(SecurityUtil.getCurrentUserId());
    }
}