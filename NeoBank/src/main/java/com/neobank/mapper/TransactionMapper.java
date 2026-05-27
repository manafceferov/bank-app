package com.neobank.mapper;

import com.neobank.dto.transaction.TransactionResponseDto;
import com.neobank.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(target = "transactionType", expression = "java(transaction.getTransactionType().name())")
    TransactionResponseDto toResponse(Transaction transaction);
}