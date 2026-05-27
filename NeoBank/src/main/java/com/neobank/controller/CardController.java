package com.neobank.controller;

import com.neobank.constant.ApiResponse;
import com.neobank.dto.card.CardRequestDto;
import com.neobank.dto.card.CardResponseDto;
import com.neobank.service.CardService;
import com.neobank.util.SecurityUtil;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/request")
    public ApiResponse<CardResponseDto> requestCard(@RequestBody CardRequestDto dto) {
        return cardService.requestCard(SecurityUtil.getCurrentUserId(), dto);
    }

    @GetMapping("/account/{accountId}")
    public ApiResponse<List<CardResponseDto>> getMyCards(@PathVariable Long accountId) {
        return cardService.getMyCards(accountId, SecurityUtil.getCurrentUserId());
    }

    @PatchMapping("/{cardId}/block")
    public ApiResponse<Void> blockCard(@PathVariable Long cardId) {
        return cardService.blockCard(cardId, SecurityUtil.getCurrentUserId());
    }
}