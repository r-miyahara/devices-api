package dev.roberto.devices.domain.persistence.repository;

import dev.roberto.devices.domain.persistence.entity.IdempotencyKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyKeyCrudRepository extends JpaRepository<IdempotencyKeyEntity, String> { }
