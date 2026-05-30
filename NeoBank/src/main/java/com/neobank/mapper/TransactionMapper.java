package com.neobank.mapper;

import com.neobank.dto.transaction.TransactionResponseDto;
import com.neobank.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(target = "transactionType", expression = "java(transaction.getTransactionType().name())")
    @Mapping(target = "fromAccountIban", source = "fromAccount.iban")
    @Mapping(target = "toAccountIban", source = "toAccount.iban")
    TransactionResponseDto toResponse(Transaction transaction);
}