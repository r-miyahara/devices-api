package dev.roberto.devices.domain.usecase.command;

import dev.roberto.devices.domain.model.DeviceState;

import java.util.Optional;
import java.util.UUID;

public record UpdateDevicePatchCommand(
  UUID id,
  Optional<String> name,
  Optional<String> brand,
  Optional<DeviceState> state
) {}
