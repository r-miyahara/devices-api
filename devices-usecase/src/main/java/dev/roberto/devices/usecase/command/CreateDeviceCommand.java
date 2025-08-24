package dev.roberto.devices.usecase.command;

import dev.roberto.devices.domain.model.DeviceState;

public record CreateDeviceCommand(String name, String brand, DeviceState state) {}
