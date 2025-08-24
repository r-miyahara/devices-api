package dev.roberto.devices.domain.model;

import dev.roberto.devices.domain.time.TimeProvider;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Device é um agregado de domínio imutável.
 * Regras específicas como "não atualizar name/brand quando IN_USE" e
 * "não deletar quando IN_USE" serão aplicadas nos use cases.
 */
public record Device(
  UUID id,
  String name,
  String brand,
  DeviceState state,
  Instant creationTime
) {
  public Device {
    Objects.requireNonNull(id, "id is required");
    Objects.requireNonNull(state, "state is required");
    Objects.requireNonNull(creationTime, "creationTime is required");
    name = normalizeNonBlank(name, "name");
    brand = normalizeNonBlank(brand, "brand");
  }

  public static Device create(String name, String brand, DeviceState initialState, TimeProvider time) {
    Objects.requireNonNull(time, "time provider is required");
    var state = initialState != null ? initialState : DeviceState.AVAILABLE;
    return new Device(UUID.randomUUID(), name, brand, state, time.now());
  }

  public Device withName(String newName) {
    return new Device(this.id, normalizeNonBlank(newName, "name"), this.brand, this.state, this.creationTime);
  }

  public Device withBrand(String newBrand) {
    return new Device(this.id, this.name, normalizeNonBlank(newBrand, "brand"), this.state, this.creationTime);
  }

  public Device withState(DeviceState newState) {
    return new Device(this.id, this.name, this.brand, Objects.requireNonNull(newState, "state"), this.creationTime);
  }

  private static String normalizeNonBlank(String value, String field) {
    Objects.requireNonNull(value, field + " is required");
    var v = value.trim();
    if (v.isEmpty()) throw new IllegalArgumentException(field + " must not be blank");
    return v;
  }
}
