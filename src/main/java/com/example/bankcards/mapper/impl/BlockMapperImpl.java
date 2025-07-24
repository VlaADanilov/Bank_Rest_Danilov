package com.example.bankcards.mapper.impl;

import com.example.bankcards.dto.response.RequestToBlockResponseDto;
import com.example.bankcards.entity.RequestToBlock;
import com.example.bankcards.mapper.BlockMapper;
import org.springframework.stereotype.Component;

@Component
public class BlockMapperImpl implements BlockMapper {
    @Override
    public RequestToBlockResponseDto toBlockDto(RequestToBlock block) {
        return RequestToBlockResponseDto.builder()
                .id(block.getId())
                .cardId(block.getCard().getId())
                .build();
    }
}
