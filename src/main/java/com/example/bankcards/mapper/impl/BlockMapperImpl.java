package com.example.bankcards.mapper.impl;

import com.example.bankcards.dto.response.RequestToBlockResponseDto;
import com.example.bankcards.entity.RequestToBlock;
import com.example.bankcards.mapper.BlockMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BlockMapperImpl implements BlockMapper {
    @Override
    public RequestToBlockResponseDto toBlockDto(RequestToBlock block) {
        log.debug("Mapping RequestToBlock entity to DTO, ID: %s".formatted(block.getId()));

        RequestToBlockResponseDto dto = RequestToBlockResponseDto.builder()
                .id(block.getId())
                .cardId(block.getCard().getId())
                .build();

        log.debug("Successfully mapped RequestToBlock ID: %s to DTO with card ID: %s".formatted(
                block.getId(), block.getCard().getId()));

        return dto;
    }
}