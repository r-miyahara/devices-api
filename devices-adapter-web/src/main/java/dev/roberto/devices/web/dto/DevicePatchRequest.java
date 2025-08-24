package dev.roberto.devices.web.dto;

import dev.roberto.devices.domain.model.DeviceState;

/**
 * Todos os campos opcionais para PATCH.
 * Validações de negócio continuam nos use cases.
 */
public record DevicePatchRequest(
  String name,
  String brand,
  DeviceState state
) {}
