package com.neobank.mapper;

import com.neobank.dto.credit.CreditResponseDto;
import com.neobank.entity.Credit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CreditMapper {
    @Mapping(target = "status", expression = "java(credit.getStatus().name())")
    CreditResponseDto toResponse(Credit credit);
}