package com.neobank.service;

import com.neobank.constant.ApiResponse;
import com.neobank.dto.card.CardRequestDto;
import com.neobank.dto.card.CardResponseDto;
import com.neobank.entity.Account;
import com.neobank.entity.Card;
import com.neobank.enums.CardStatus;
import com.neobank.enums.Messages;
import com.neobank.mapper.CardMapper;
import com.neobank.repository.AccountRepository;
import com.neobank.repository.CardRepository;
import com.neobank.util.CardUtil;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final CardMapper cardMapper;

    public CardService(CardRepository cardRepository,
                       AccountRepository accountRepository,
                       CardMapper cardMapper) {
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
        this.cardMapper = cardMapper;
    }

    public ApiResponse<CardResponseDto> requestCard(Long userId, CardRequestDto dto) {
        Account account = accountRepository.findByIdAndDeletedFalse(dto.getAccountId())
                .orElseThrow(() -> new RuntimeException(Messages.NOT_FOUND.name()));
        if (!account.getUser().getId().equals(userId))
            throw new RuntimeException(Messages.FORBIDDEN.name());

        String cardNumber = CardUtil.generateCardNumber();
        while (cardRepository.existsByCardNumber(cardNumber)) {
            cardNumber = CardUtil.generateCardNumber();
        }

        Card card = new Card();
        card.setAccount(account);
        card.setCardNumber(cardNumber);
        card.setCardHolderName(account.getUser().getFirstName() + " " + account.getUser().getLastName());
        card.setCardType(dto.getCardType());
        card.setCvv(CardUtil.generateCvv());
        card.setExpiryDate(LocalDate.now().plusYears(3));
        card.setStatus(CardStatus.PENDING);
        cardRepository.save(card);

        return new ApiResponse<>(true, cardMapper.toResponse(card), Messages.CREATED.name());
    }

    public ApiResponse<List<CardResponseDto>> getMyCards(Long accountId, Long userId) {
        Account account = accountRepository.findByIdAndDeletedFalse(accountId)
                .orElseThrow(() -> new RuntimeException(Messages.NOT_FOUND.name()));
        if (!account.getUser().getId().equals(userId))
            throw new RuntimeException(Messages.FORBIDDEN.name());

        List<CardResponseDto> list = cardRepository
                .findByAccountIdAndDeletedFalse(accountId)
                .stream()
                .map(cardMapper::toResponse)
                .collect(Collectors.toList());
        return new ApiResponse<>(true, list, Messages.SUCCESS.name());
    }

    public ApiResponse<Void> blockCard(Long cardId, Long userId) {
        Card card = cardRepository.findByIdAndDeletedFalse(cardId)
                .orElseThrow(() -> new RuntimeException(Messages.NOT_FOUND.name()));
        if (!card.getAccount().getUser().getId().equals(userId))
            throw new RuntimeException(Messages.FORBIDDEN.name());
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
        return new ApiResponse<>(true, Messages.UPDATED.name());
    }
}