package com.neobank.controller;

import com.neobank.constant.ApiResponse;
import com.neobank.dto.transaction.TransactionResponseDto;
import com.neobank.dto.transaction.TransferRequestDto;
import com.neobank.service.TransactionService;
import com.neobank.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    public ApiResponse<TransactionResponseDto> transfer(@RequestBody TransferRequestDto dto) {
        return transactionService.transfer(SecurityUtil.getCurrentUserId(), dto);
    }

    @GetMapping("/account/{accountId}")
    public ApiResponse<Page<TransactionResponseDto>> getHistory(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Sort problemi aradan qaldırılır
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return transactionService.getHistory(accountId, SecurityUtil.getCurrentUserId(), pageable);
    }
}