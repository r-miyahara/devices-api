package dev.roberto.devices.persistence.repository;

import dev.roberto.devices.domain.model.DeviceState;
import dev.roberto.devices.persistence.entity.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaDeviceCrudRepository extends JpaRepository<DeviceEntity, UUID> {
  List<DeviceEntity> findByBrand(String brand);
  List<DeviceEntity> findByState(DeviceState state);
}
