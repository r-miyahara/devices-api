package dev.roberto.devices.web;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Store simples em memória para POST idempotente.
 * Pode ser substituído por uma implementação persistente futuramente.
 */
@Service
class IdempotencyService {
  private final ConcurrentMap<String, UUID> map = new ConcurrentHashMap<>();

  Optional<UUID> get(String key) { return Optional.ofNullable(map.get(key)); }

  void putIfAbsent(String key, UUID id) { map.putIfAbsent(key, id); }
}
