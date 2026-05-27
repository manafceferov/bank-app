package com.neobank.mapper;

import com.neobank.dto.account.AccountResponseDto;
import com.neobank.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    @Mapping(target = "accountType", expression = "java(account.getAccountType().name())")
    @Mapping(target = "status", expression = "java(account.getStatus().name())")
    AccountResponseDto toResponse(Account account);
}