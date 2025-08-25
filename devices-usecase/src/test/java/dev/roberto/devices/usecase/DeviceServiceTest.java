package dev.roberto.devices.usecase;

import dev.roberto.devices.domain.model.Device;
import dev.roberto.devices.domain.model.DeviceState;
import dev.roberto.devices.domain.port.DeviceRepository;
import dev.roberto.devices.domain.time.TimeProvider;
import dev.roberto.devices.usecase.command.CreateDeviceCommand;
import dev.roberto.devices.usecase.command.UpdateDevicePatchCommand;
import dev.roberto.devices.usecase.command.UpdateDevicePutCommand;
import dev.roberto.devices.usecase.exception.DomainRuleViolationException;
import dev.roberto.devices.usecase.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class DeviceServiceTest {

  private InMemoryDeviceRepository repo;
  private FixedTimeProvider time;
  private DeviceService service;

  @BeforeEach
  void setUp() {
    repo = new InMemoryDeviceRepository();
    time = new FixedTimeProvider(Instant.parse("2025-01-01T00:00:00Z"));
    service = new DeviceService(repo, time);
  }

  @Test
  void create_shouldPersistWithCreationTimeAndDefaultState() {
    var created = service.create(new CreateDeviceCommand("WS-01", "Lenovo", null));

    assertNotNull(created.id());
    assertEquals("WS-01", created.name());
    assertEquals("Lenovo", created.brand());
    assertEquals(DeviceState.AVAILABLE, created.state()); // default
    assertEquals(time.now(), created.creationTime());

    // repo should contain it
    assertEquals(1, repo.store.size());
  }

  @Test
  void create_shouldHonorExplicitState() {
    var created = service.create(new CreateDeviceCommand("Lap-01", "Dell", DeviceState.IN_USE));
    assertEquals(DeviceState.IN_USE, created.state());
  }

  @Test
  void get_shouldThrowWhenNotFound() {
    var unknownId = UUID.randomUUID();
    assertThrows(NotFoundException.class, () -> service.get(unknownId));
  }

  @Test
  void listByBrand_and_listByState_shouldFilter() {
    service.create(new CreateDeviceCommand("A", "Apple", DeviceState.AVAILABLE));
    service.create(new CreateDeviceCommand("B", "Apple", DeviceState.INACTIVE));
    service.create(new CreateDeviceCommand("C", "Lenovo", DeviceState.IN_USE));

    var apple = service.listByBrand("Apple");
    assertEquals(2, apple.size());

    var inUse = service.listByState(DeviceState.IN_USE);
    assertEquals(1, inUse.size());
    assertEquals("C", inUse.get(0).name());
  }

  @Test
  void updatePut_shouldAllowChangeWhenNotInUse_andPreserveCreationTime() {
    var d = service.create(new CreateDeviceCommand("WS-01", "Lenovo", DeviceState.AVAILABLE));
    var updated = service.updatePut(new UpdateDevicePutCommand(d.id(), "WS-02", "Lenovo", DeviceState.INACTIVE));

    assertEquals("WS-02", updated.name());
    assertEquals(DeviceState.INACTIVE, updated.state());
    assertEquals(d.creationTime(), updated.creationTime()); // immutable
  }

  @Test
  void updatePut_shouldBlockNameOrBrandChange_whenCurrentIsInUse() {
    var d = service.create(new CreateDeviceCommand("WS-01", "Lenovo", DeviceState.IN_USE));
    assertThrows(DomainRuleViolationException.class,
      () -> service.updatePut(new UpdateDevicePutCommand(d.id(), "WS-02", "Lenovo", DeviceState.IN_USE)));
  }

  @Test
  void updatePut_shouldBlockNameOrBrandChange_whenSettingStateToInUse() {
    var d = service.create(new CreateDeviceCommand("WS-01", "Lenovo", DeviceState.AVAILABLE));
    assertThrows(DomainRuleViolationException.class,
      () -> service.updatePut(new UpdateDevicePutCommand(d.id(), "WS-02", "Lenovo", DeviceState.IN_USE)));
  }

  @Test
  void updatePatch_shouldApplyPartialChanges_whenAllowed() {
    var d = service.create(new CreateDeviceCommand("WS-01", "Lenovo", DeviceState.AVAILABLE));

    var updated = service.updatePatch(new UpdateDevicePatchCommand(
      d.id(),
      Optional.empty(),
      Optional.of("HP"),
      Optional.of(DeviceState.INACTIVE)
    ));

    assertEquals("WS-01", updated.name()); // unchanged
    assertEquals("HP", updated.brand());
    assertEquals(DeviceState.INACTIVE, updated.state());
    assertEquals(d.creationTime(), updated.creationTime());
  }

  @Test
  void updatePatch_shouldBlockNameOrBrandChange_whenCurrentIsInUse() {
    var d = service.create(new CreateDeviceCommand("WS-01", "Lenovo", DeviceState.IN_USE));
    assertThrows(DomainRuleViolationException.class, () ->
      service.updatePatch(new UpdateDevicePatchCommand(
        d.id(),
        Optional.of("WS-02"),
        Optional.empty(),
        Optional.empty()
      )));
  }

  @Test
  void updatePatch_shouldBlockNameOrBrandChange_whenBecomingInUse() {
    var d = service.create(new CreateDeviceCommand("WS-01", "Lenovo", DeviceState.AVAILABLE));
    assertThrows(DomainRuleViolationException.class, () ->
      service.updatePatch(new UpdateDevicePatchCommand(
        d.id(),
        Optional.of("WS-02"),
        Optional.empty(),
        Optional.of(DeviceState.IN_USE)
      )));
  }

  @Test
  void delete_shouldBlockWhenInUse() {
    var d = service.create(new CreateDeviceCommand("WS-01", "Lenovo", DeviceState.IN_USE));
    assertThrows(DomainRuleViolationException.class, () -> service.delete(d.id()));
  }

  @Test
  void delete_shouldWorkWhenNotInUse() {
    var d = service.create(new CreateDeviceCommand("WS-01", "Lenovo", DeviceState.AVAILABLE));
    assertDoesNotThrow(() -> service.delete(d.id()));
    assertTrue(repo.findById(d.id()).isEmpty());
  }

  // ----------------- helpers (fakes) -----------------

  static class FixedTimeProvider implements TimeProvider {
    private final Instant fixed;
    FixedTimeProvider(Instant fixed) { this.fixed = fixed; }
    @Override public Instant now() { return fixed; }
  }

  static class InMemoryDeviceRepository implements DeviceRepository {
    final Map<UUID, Device> store = new ConcurrentHashMap<>();

    @Override
    public Device save(Device device) {
      store.put(device.id(), device);
      return device;
    }

    @Override
    public Optional<Device> findById(UUID id) {
      return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Device> findAll() {
      return new ArrayList<>(store.values());
    }

    @Override
    public List<Device> findByBrand(String brand) {
      return store.values().stream().filter(d -> d.brand().equals(brand)).toList();
    }

    @Override
    public List<Device> findByState(DeviceState state) {
      return store.values().stream().filter(d -> d.state() == state).toList();
    }

    @Override
    public List<Device> findAllPaged(int page, int size) {
      return null;
    }

    @Override
    public List<Device> findByBrandPaged(String brand, int page, int size) {
      return null;
    }

    @Override
    public List<Device> findByStatePaged(DeviceState state, int page, int size) {
      return null;
    }

    @Override
    public long countAll() {
      return 0;
    }

    @Override
    public long countByBrand(String brand) {
      return 0;
    }

    @Override
    public long countByState(DeviceState state) {
      return 0;
    }

    @Override
    public void deleteById(UUID id) {
      store.remove(id);
    }
  }
}
