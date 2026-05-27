package com.neobank.mapper;

import com.neobank.dto.card.CardResponseDto;
import com.neobank.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CardMapper {
    @Mapping(target = "cardType", expression = "java(card.getCardType().name())")
    @Mapping(target = "status", expression = "java(card.getStatus().name())")
    @Mapping(target = "cardNumber", expression = "java(\"**** **** **** \" + card.getCardNumber().substring(12))")
    CardResponseDto toResponse(Card card);
}