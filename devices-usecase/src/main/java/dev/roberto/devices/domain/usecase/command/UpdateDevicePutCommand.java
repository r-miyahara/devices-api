package dev.roberto.devices.domain.usecase.command;

import dev.roberto.devices.domain.model.DeviceState;

import java.util.UUID;

public record UpdateDevicePutCommand(UUID id, String name, String brand, DeviceState state) {}
