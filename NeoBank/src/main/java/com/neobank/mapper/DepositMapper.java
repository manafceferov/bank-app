package com.neobank.mapper;

import com.neobank.dto.deposit.DepositResponseDto;
import com.neobank.entity.Deposit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DepositMapper {
    @Mapping(target = "status", expression = "java(deposit.getStatus().name())")
    DepositResponseDto toResponse(Deposit deposit);
}