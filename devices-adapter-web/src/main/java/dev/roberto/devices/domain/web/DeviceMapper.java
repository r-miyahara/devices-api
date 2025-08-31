package dev.roberto.devices.domain.web;

import dev.roberto.devices.domain.model.Device;
import dev.roberto.devices.domain.web.dto.DeviceResponse;

final class DeviceMapper {
  private DeviceMapper() {}

  static DeviceResponse toResponse(Device d) {
    return new DeviceResponse(d.id(), d.name(), d.brand(), d.state(), d.creationTime());
  }
}
