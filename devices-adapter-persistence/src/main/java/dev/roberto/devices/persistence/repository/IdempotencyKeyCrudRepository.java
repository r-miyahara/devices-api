package dev.roberto.devices.persistence.repository;

import dev.roberto.devices.persistence.entity.IdempotencyKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyKeyCrudRepository extends JpaRepository<IdempotencyKeyEntity, String> { }
