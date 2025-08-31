package dev.roberto.devices.domain.web;

import dev.roberto.devices.domain.port.IdempotencyStore;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
class IdempotencyService {

  private static final Duration DEFAULT_TTL = Duration.ofHours(24);

  private final IdempotencyStore store;

  IdempotencyService(IdempotencyStore store) {
    this.store = store;
  }

  Optional<UUID> get(String key) {
    return store.get(key);
  }

  void putIfAbsent(String key, UUID id) {
    store.saveIfAbsent(key, id, Instant.now(), DEFAULT_TTL);
  }
}
