package com.example.bankcards.mapper;

import com.example.bankcards.dto.response.RequestToBlockResponseDto;
import com.example.bankcards.entity.RequestToBlock;

public interface BlockMapper {
    RequestToBlockResponseDto toBlockDto(RequestToBlock block);
}
