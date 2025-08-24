package dev.roberto.devices.web.dto;

import dev.roberto.devices.domain.model.DeviceState;
import jakarta.validation.constraints.NotBlank;

public record DeviceRequest(
  @NotBlank String name,
  @NotBlank String brand,
  DeviceState state // opcional
) {}
