package dev.roberto.devices.web;

import dev.roberto.devices.domain.model.Device;
import dev.roberto.devices.domain.model.DeviceState;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DeviceMapperTest {

  @Test
  void toResponse_shouldMapAllFields() {
    var id = UUID.randomUUID();
    var created = Instant.parse("2025-01-01T00:00:00Z");
    var d = new Device(id, "WS-01", "Lenovo", DeviceState.AVAILABLE, created);

    var resp = DeviceMapper.toResponse(d);

    assertEquals(id, resp.id());
    assertEquals("WS-01", resp.name());
    assertEquals("Lenovo", resp.brand());
    assertEquals(DeviceState.AVAILABLE, resp.state());
    assertEquals(created, resp.creationTime());
  }
}
