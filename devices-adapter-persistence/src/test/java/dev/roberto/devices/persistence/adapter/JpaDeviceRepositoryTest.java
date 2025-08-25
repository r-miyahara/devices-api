package dev.roberto.devices.persistence.adapter;

import dev.roberto.devices.domain.model.Device;
import dev.roberto.devices.domain.model.DeviceState;
import dev.roberto.devices.persistence.entity.DeviceEntity;
import dev.roberto.devices.persistence.repository.JpaDeviceCrudRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
  "spring.flyway.enabled=false",
  "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Import(JpaDeviceRepository.class)
class JpaDeviceRepositoryTest {

  @Autowired JpaDeviceRepository repo;
  @Autowired JpaDeviceCrudRepository crud; // sanity checks

  @Test
  void save_and_findById_shouldRoundTrip() {
    var id = UUID.randomUUID();
    var d = new Device(id, "WS-01", "Lenovo", DeviceState.AVAILABLE, Instant.parse("2025-01-01T00:00:00Z"));

    var saved = repo.save(d);
    assertEquals(d.id(), saved.id());
    assertEquals(d.name(), saved.name());
    assertEquals(d.brand(), saved.brand());
    assertEquals(d.state(), saved.state());
    assertEquals(d.creationTime(), saved.creationTime());

    var found = repo.findById(id).orElseThrow();
    assertEquals("WS-01", found.name());

    // sanity: entity really exists
    assertTrue(crud.findById(id).isPresent());
  }

  @Test
  void findByBrand_and_findByState_shouldFilter() {
    repo.save(new Device(UUID.randomUUID(), "A", "Apple", DeviceState.AVAILABLE, Instant.now()));
    repo.save(new Device(UUID.randomUUID(), "B", "Apple", DeviceState.INACTIVE, Instant.now()));
    repo.save(new Device(UUID.randomUUID(), "C", "Lenovo", DeviceState.IN_USE, Instant.now()));

    var apple = repo.findByBrand("Apple");
    assertEquals(2, apple.size());

    var inUse = repo.findByState(DeviceState.IN_USE);
    assertEquals(1, inUse.size());
    assertEquals("C", inUse.get(0).name());
  }

  @Test
  void deleteById_shouldRemove() {
    var id = UUID.randomUUID();
    repo.save(new Device(id, "X", "HP", DeviceState.AVAILABLE, Instant.now()));
    repo.deleteById(id);
    assertTrue(repo.findById(id).isEmpty());
  }
}
