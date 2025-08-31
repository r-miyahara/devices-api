package dev.roberto.devices.domain.web.dto;

import dev.roberto.devices.domain.model.DeviceState;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DevicePatchRequest(
  @Size(min = 1, max = 255)
  @Pattern(regexp = "^[\\p{L}0-9 ._\\-]{1,255}$", message = "invalid characters")
  String name,

  @Size(min = 1, max = 255)
  @Pattern(regexp = "^[\\p{L}0-9 ._\\-]{1,255}$", message = "invalid characters")
  String brand,

  DeviceState state
) {}
