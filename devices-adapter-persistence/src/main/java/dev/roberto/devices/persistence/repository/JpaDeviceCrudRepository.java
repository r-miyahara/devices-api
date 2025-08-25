package dev.roberto.devices.persistence.repository;

import dev.roberto.devices.domain.model.DeviceState;
import dev.roberto.devices.persistence.entity.DeviceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaDeviceCrudRepository extends JpaRepository<DeviceEntity, UUID> {
  List<DeviceEntity> findByBrand(String brand);
  List<DeviceEntity> findByState(DeviceState state);

  // novos (paginados)
  Page<DeviceEntity> findAll(Pageable pageable);
  Page<DeviceEntity> findByBrand(String brand, Pageable pageable);
  Page<DeviceEntity> findByState(DeviceState state, Pageable pageable);

  long countByBrand(String brand);
  long countByState(DeviceState state);
}
