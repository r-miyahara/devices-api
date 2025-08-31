package dev.roberto.devices.domain.time;

import java.time.Instant;

public interface TimeProvider {
  Instant now();
}
