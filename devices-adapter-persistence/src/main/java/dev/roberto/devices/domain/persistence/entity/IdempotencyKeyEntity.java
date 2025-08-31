package dev.roberto.devices.domain.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "idempotency_keys")
public class IdempotencyKeyEntity {

  @Id
  @Column(name = "ikey", nullable = false, length = 200, updatable = false)
  private String key;

  @Column(name = "resource_id", nullable = false)
  private UUID resourceId;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  protected IdempotencyKeyEntity() {}

  public IdempotencyKeyEntity(String key, UUID resourceId, Instant createdAt, Instant expiresAt) {
    this.key = key;
    this.resourceId = resourceId;
    this.createdAt = createdAt;
    this.expiresAt = expiresAt;
  }

  public String getKey() { return key; }
  public UUID getResourceId() { return resourceId; }
  public Instant getCreatedAt() { return createdAt; }
  public Instant getExpiresAt() { return expiresAt; }

  public void setResourceId(UUID resourceId) { this.resourceId = resourceId; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
  public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}
