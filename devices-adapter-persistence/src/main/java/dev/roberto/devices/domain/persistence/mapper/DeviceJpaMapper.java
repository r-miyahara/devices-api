package dev.roberto.devices.domain.persistence.mapper;

import dev.roberto.devices.domain.model.Device;
import dev.roberto.devices.domain.persistence.entity.DeviceEntity;

public final class DeviceJpaMapper {
  private DeviceJpaMapper() {}

  public static DeviceEntity toEntity(Device d) {
    return new DeviceEntity(d.id(), d.name(), d.brand(), d.state(), d.creationTime());
  }

  public static Device toDomain(DeviceEntity e) {
    return new Device(e.getId(), e.getName(), e.getBrand(), e.getState(), e.getCreationTime());
  }
}
