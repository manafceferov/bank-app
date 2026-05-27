package com.neobank.controller;

import com.neobank.constant.ApiResponse;
import com.neobank.dto.account.AccountCreateDto;
import com.neobank.dto.account.AccountResponseDto;
import com.neobank.service.AccountService;
import com.neobank.util.SecurityUtil;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ApiResponse<AccountResponseDto> create(@RequestBody AccountCreateDto dto) {
        return accountService.create(SecurityUtil.getCurrentUserId(), dto);
    }

    @GetMapping
    public ApiResponse<List<AccountResponseDto>> getMyAccounts() {
        return accountService.getMyAccounts(SecurityUtil.getCurrentUserId());
    }

    @GetMapping("/{id}")
    public ApiResponse<AccountResponseDto> getById(@PathVariable Long id) {
        return accountService.getById(id, SecurityUtil.getCurrentUserId());
    }

    @PatchMapping("/{id}/block")
    public ApiResponse<Void> block(@PathVariable Long id) {
        return accountService.block(id);
    }
}