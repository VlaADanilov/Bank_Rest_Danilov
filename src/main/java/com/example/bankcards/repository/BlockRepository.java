package com.example.bankcards.repository;

import com.example.bankcards.entity.RequestToBlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BlockRepository extends JpaRepository<RequestToBlock, UUID> {
}
