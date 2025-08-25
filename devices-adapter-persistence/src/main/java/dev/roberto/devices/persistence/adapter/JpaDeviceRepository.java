package dev.roberto.devices.persistence.adapter;

import dev.roberto.devices.domain.model.Device;
import dev.roberto.devices.domain.model.DeviceState;
import dev.roberto.devices.domain.port.DeviceRepository;
import dev.roberto.devices.persistence.mapper.DeviceJpaMapper;
import dev.roberto.devices.persistence.repository.JpaDeviceCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;

@Repository
public class JpaDeviceRepository implements DeviceRepository {

  private final JpaDeviceCrudRepository jpa;

  public JpaDeviceRepository(JpaDeviceCrudRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public Device save(Device device) {
    var saved = jpa.save(DeviceJpaMapper.toEntity(device));
    return DeviceJpaMapper.toDomain(saved);
  }

  @Override
  public Optional<Device> findById(UUID id) {
    return jpa.findById(id).map(DeviceJpaMapper::toDomain);
  }

  @Override
  public List<Device> findAll() {
    return jpa.findAll().stream().map(DeviceJpaMapper::toDomain).toList();
  }

  @Override
  public List<Device> findByBrand(String brand) {
    return jpa.findByBrand(brand).stream().map(DeviceJpaMapper::toDomain).toList();
  }

  @Override
  public List<Device> findByState(DeviceState state) {
    return jpa.findByState(state).stream().map(DeviceJpaMapper::toDomain).toList();
  }

  @Override
  public List<Device> findAllPaged(int page, int size) {
    return jpa.findAll(PageRequest.of(page, size))
      .map(DeviceJpaMapper::toDomain).toList();
  }

  @Override
  public List<Device> findByBrandPaged(String brand, int page, int size) {
    return jpa.findByBrand(brand, PageRequest.of(page, size))
      .map(DeviceJpaMapper::toDomain).toList();
  }

  @Override
  public List<Device> findByStatePaged(DeviceState state, int page, int size) {
    return jpa.findByState(state, PageRequest.of(page, size))
      .map(DeviceJpaMapper::toDomain).toList();
  }

  @Override
  public long countAll() {
    return jpa.count();
  }

  @Override
  public long countByBrand(String brand) {
    return jpa.countByBrand(brand);
  }

  @Override
  public long countByState(DeviceState state) {
    return jpa.countByState(state);
  }

  @Override
  public void deleteById(UUID id) {
    jpa.deleteById(id);
  }
}
