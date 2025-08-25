package dev.roberto.devices.web.dto;

import dev.roberto.devices.domain.model.DeviceState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DeviceRequest(
  @NotBlank @Size(max = 255)
  @Pattern(regexp = "^[\\p{L}0-9 ._\\-]{1,255}$", message = "invalid characters")
  String name,

  @NotBlank @Size(max = 255)
  @Pattern(regexp = "^[\\p{L}0-9 ._\\-]{1,255}$", message = "invalid characters")
  String brand,


  DeviceState state
) {}
