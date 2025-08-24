package dev.roberto.devices.web;

import dev.roberto.devices.domain.model.Device;
import dev.roberto.devices.web.dto.DeviceResponse;

final class DeviceMapper {
  private DeviceMapper() {}

  static DeviceResponse toResponse(Device d) {
    return new DeviceResponse(d.id(), d.name(), d.brand(), d.state(), d.creationTime());
  }
}
