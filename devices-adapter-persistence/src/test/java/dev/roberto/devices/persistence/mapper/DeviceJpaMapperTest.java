package dev.roberto.devices.persistence.mapper;

import dev.roberto.devices.domain.model.Device;
import dev.roberto.devices.domain.model.DeviceState;
import dev.roberto.devices.persistence.entity.DeviceEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DeviceJpaMapperTest {

  @Test
  void toEntity_and_toDomain_shouldMapAllFields() {
    var id = UUID.randomUUID();
    var t = Instant.parse("2025-01-01T00:00:00Z");
    var d = new Device(id, "WS-01", "Lenovo", DeviceState.AVAILABLE, t);

    var e = DeviceJpaMapper.toEntity(d);
    assertEquals(id, e.getId());
    assertEquals("WS-01", e.getName());
    assertEquals("Lenovo", e.getBrand());
    assertEquals(DeviceState.AVAILABLE, e.getState());
    assertEquals(t, e.getCreationTime());

    var back = DeviceJpaMapper.toDomain(e);
    assertEquals(d, back);
  }
}
