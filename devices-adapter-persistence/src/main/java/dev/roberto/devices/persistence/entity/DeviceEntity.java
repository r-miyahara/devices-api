package dev.roberto.devices.persistence.entity;

import dev.roberto.devices.domain.model.DeviceState;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "devices")
public class DeviceEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "name", nullable = false, length = 255)
  private String name;

  @Column(name = "brand", nullable = false, length = 255)
  private String brand;

  @Enumerated(EnumType.STRING)
  @Column(name = "state", nullable = false, length = 20)
  private DeviceState state;

  @Column(name = "creation_time", nullable = false)
  private Instant creationTime;

  protected DeviceEntity() { /* JPA */ }

  public DeviceEntity(UUID id, String name, String brand, DeviceState state, Instant creationTime) {
    this.id = id;
    this.name = name;
    this.brand = brand;
    this.state = state;
    this.creationTime = creationTime;
  }

  public UUID getId() { return id; }
  public String getName() { return name; }
  public String getBrand() { return brand; }
  public DeviceState getState() { return state; }
  public Instant getCreationTime() { return creationTime; }

  public void setId(UUID id) { this.id = id; }
  public void setName(String name) { this.name = name; }
  public void setBrand(String brand) { this.brand = brand; }
  public void setState(DeviceState state) { this.state = state; }
  public void setCreationTime(Instant creationTime) { this.creationTime = creationTime; }
}
