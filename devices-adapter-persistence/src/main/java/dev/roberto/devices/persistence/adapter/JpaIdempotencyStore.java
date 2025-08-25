package dev.roberto.devices.persistence.adapter;

import dev.roberto.devices.domain.port.IdempotencyStore;
import dev.roberto.devices.persistence.entity.IdempotencyKeyEntity;
import dev.roberto.devices.persistence.repository.IdempotencyKeyCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaIdempotencyStore implements IdempotencyStore {

  private final IdempotencyKeyCrudRepository repo;

  public JpaIdempotencyStore(IdempotencyKeyCrudRepository repo) {
    this.repo = repo;
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<UUID> get(String key) {
    return repo.findById(key)
      .filter(e -> e.getExpiresAt().isAfter(Instant.now()))
      .map(IdempotencyKeyEntity::getResourceId);
  }

  @Override
  @Transactional
  public void saveIfAbsent(String key, UUID resourceId, Instant now, Duration ttl) {
    var opt = repo.findById(key);
    var expiresAt = now.plus(ttl);

    if (opt.isEmpty()) {
      repo.save(new IdempotencyKeyEntity(key, resourceId, now, expiresAt));
      return;
    }

    var existing = opt.get();
    if (existing.getExpiresAt().isBefore(now)) {
      existing.setResourceId(resourceId);
      existing.setCreatedAt(now);
      existing.setExpiresAt(expiresAt);
      repo.save(existing);
    }
  }

  @Override
  @Transactional
  public void purgeExpired(Instant now) {
    repo.findAll().stream()
      .filter(e -> e.getExpiresAt().isBefore(now))
      .forEach(repo::delete);
  }
}
