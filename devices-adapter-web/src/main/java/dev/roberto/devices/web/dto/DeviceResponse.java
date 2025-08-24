package dev.roberto.devices.web.dto;

import dev.roberto.devices.domain.model.DeviceState;

import java.time.Instant;
import java.util.UUID;

public record DeviceResponse(
  UUID id,
  String name,
  String brand,
  DeviceState state,
  Instant creationTime
) {}
