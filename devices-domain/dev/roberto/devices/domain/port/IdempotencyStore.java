package dev.roberto.devices.domain.port;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface IdempotencyStore {
  Optional<UUID> get(String key);
  void saveIfAbsent(String key, UUID resourceId, Instant now, Duration ttl);
  void purgeExpired(Instant now);
}
