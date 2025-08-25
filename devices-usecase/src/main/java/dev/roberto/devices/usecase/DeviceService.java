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
import java.util.Optional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class DeviceService {

  private final DeviceRepository repository;
  private final TimeProvider time;

  public DeviceService(DeviceRepository repository, TimeProvider time) {
    this.repository = Objects.requireNonNull(repository);
    this.time = Objects.requireNonNull(time);
  }

  // CREATE
  public Device create(CreateDeviceCommand cmd) {
    var device = Device.create(cmd.name(), cmd.brand(), cmd.state(), time);
    return repository.save(device);
  }

  // READ (one)
  public Device get(UUID id) {
    return repository.findById(id)
      .orElseThrow(() -> new NotFoundException("Device %s not found".formatted(id)));
  }

  // READ (all)
  public List<Device> listAll() {
    return repository.findAll();
  }

  // FILTERS
  public List<Device> listByBrand(String brand) {
    return repository.findByBrand(brand);
  }

  public List<Device> listByState(DeviceState state) {
    return repository.findByState(state);
  }

  public PageResult<Device> listPaged(Optional<String> brand, Optional<DeviceState> state, int page, int size) {
    if (brand.isEmpty() && state.isEmpty()) {
      var items = repository.findAllPaged(page, size);
      var total = repository.countAll();
      return new PageResult<>(items, total, page, size);
    }
    if (brand.isPresent() && state.isEmpty()) {
      var items = repository.findByBrandPaged(brand.get(), page, size);
      var total = repository.countByBrand(brand.get());
      return new PageResult<>(items, total, page, size);
    }
    if (state.isPresent() && brand.isEmpty()) {
      var items = repository.findByStatePaged(state.get(), page, size);
      var total = repository.countByState(state.get());
      return new PageResult<>(items, total, page, size);
    }
    var byBrand = repository.findByBrand(brand.get());
    var filtered = byBrand.stream().filter(d -> d.state() == state.get()).toList();
    var total = filtered.size();
    int from = Math.min(page * size, total);
    int to = Math.min(from + size, total);
    var pageItems = filtered.subList(from, to);
    return new PageResult<>(pageItems, total, page, size);
  }

  // UPDATE - PUT (replace all fields but keep creationTime)
  public Device updatePut(UpdateDevicePutCommand cmd) {
    var current = get(cmd.id());

    // Regra: se o dispositivo está IN_USE, não pode alterar name/brand
    if (current.state() == DeviceState.IN_USE &&
      (!current.name().equals(cmd.name()) || !current.brand().equals(cmd.brand()))) {
      throw new DomainRuleViolationException("Cannot change name/brand when device is IN_USE");
    }

    // Regra adicional: se o novo estado será IN_USE, não permitir que name/brand mudem na mesma operação
    if (cmd.state() == DeviceState.IN_USE &&
      (!current.name().equals(cmd.name()) || !current.brand().equals(cmd.brand()))) {
      throw new DomainRuleViolationException("Cannot change name/brand when setting state to IN_USE");
    }

    var updated = current
      .withName(cmd.name())
      .withBrand(cmd.brand())
      .withState(cmd.state() != null ? cmd.state() : current.state());

    return repository.save(updated);
  }

  // UPDATE - PATCH (partial)
  public Device updatePatch(UpdateDevicePatchCommand cmd) {
    var current = get(cmd.id());

    var newName  = cmd.name().orElse(current.name());
    var newBrand = cmd.brand().orElse(current.brand());
    var newState = cmd.state().orElse(current.state());

    // Mesmas regras de bloqueio de name/brand em IN_USE (estado atual ou resultante)
    boolean changingNameOrBrand = !current.name().equals(newName) || !current.brand().equals(newBrand);
    if ((current.state() == DeviceState.IN_USE && changingNameOrBrand) ||
      (newState == DeviceState.IN_USE && changingNameOrBrand)) {
      throw new DomainRuleViolationException("Cannot change name/brand while device is or becomes IN_USE");
    }

    var updated = current;
    if (!current.name().equals(newName))  updated = updated.withName(newName);
    if (!current.brand().equals(newBrand)) updated = updated.withBrand(newBrand);
    if (current.state() != newState)       updated = updated.withState(newState);

    return repository.save(updated);
  }

  // DELETE
  public void delete(UUID id) {
    var current = get(id);
    if (current.state() == DeviceState.IN_USE) {
      throw new DomainRuleViolationException("Cannot delete a device in IN_USE state");
    }
    repository.deleteById(id);
  }
}
