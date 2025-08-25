package dev.roberto.devices.domain.port;

import dev.roberto.devices.domain.model.Device;
import dev.roberto.devices.domain.model.DeviceState;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository {
  Device save(Device device);

  Optional<Device> findById(UUID id);

  List<Device> findAll();
  List<Device> findByBrand(String brand);
  List<Device> findByState(DeviceState state);


  List<Device> findAllPaged(int page, int size);
  List<Device> findByBrandPaged(String brand, int page, int size);
  List<Device> findByStatePaged(DeviceState state, int page, int size);

  long countAll();
  long countByBrand(String brand);
  long countByState(DeviceState state);

  void deleteById(UUID id);
}
