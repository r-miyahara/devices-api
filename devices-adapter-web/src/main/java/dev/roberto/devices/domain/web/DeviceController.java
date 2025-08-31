package dev.roberto.devices.domain.web;

import dev.roberto.devices.domain.model.DeviceState;
import dev.roberto.devices.domain.usecase.DeviceService;
import dev.roberto.devices.domain.web.dto.DeviceResponse;
import dev.roberto.devices.domain.usecase.command.CreateDeviceCommand;
import dev.roberto.devices.domain.usecase.command.UpdateDevicePatchCommand;
import dev.roberto.devices.domain.usecase.command.UpdateDevicePutCommand;
import dev.roberto.devices.domain.web.dto.DevicePatchRequest;
import dev.roberto.devices.domain.web.dto.DeviceRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.URI;
import java.util.*;

@Tag(name = "Devices", description = "Devices CRUD and filters")
@RestController
@RequestMapping("/devices")
public class DeviceController {

  private final DeviceService service;
  private final IdempotencyService idempotencyService;
  public DeviceController(DeviceService service, IdempotencyService idempotencyService) {
    this.service = service;
    this.idempotencyService = idempotencyService;
  }


  @Operation(summary = "Create a device")
  @PostMapping
  public ResponseEntity<DeviceResponse> create(
    @RequestHeader(value = "Idempotency-Key", required = false) String idemKey,
    @Valid @RequestBody DeviceRequest req
  ) {
    if (idemKey != null && !idemKey.isBlank()) {
      var existing = idempotencyService.get(idemKey);
      if (existing.isPresent()) {
        var d = service.get(existing.get());
        return ResponseEntity.ok()
          .header("Idempotency-Replay", "true")
          .header("ETag", EtagUtil.etagFor(d))
          .location(URI.create("/devices/" + d.id()))
          .body(DeviceMapper.toResponse(d));
      }
    }

    var created = service.create(new CreateDeviceCommand(req.name(), req.brand(), req.state()));

    if (idemKey != null && !idemKey.isBlank()) {
      idempotencyService.putIfAbsent(idemKey, created.id());
    }

    return ResponseEntity.created(URI.create("/devices/" + created.id()))
      .header("ETag", EtagUtil.etagFor(created))
      .body(DeviceMapper.toResponse(created));
  }

  @Operation(summary = "Get a device by ID")
  @GetMapping("/{id}")
  public ResponseEntity<DeviceResponse> get(
    @PathVariable UUID id,
    @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch
  ) {
    var d = service.get(id);
    var etag = EtagUtil.etagFor(d);
    if (ifNoneMatch != null && ifNoneMatch.equals(etag)) {
      return ResponseEntity.status(304)
        .eTag(etag)
        .header("Cache-Control", "max-age=60, must-revalidate")
        .build();
    }
    return ResponseEntity.ok()
      .eTag(etag)
      .header("Cache-Control", "max-age=60, must-revalidate")
      .body(DeviceMapper.toResponse(d));
  }

  @Operation(summary = "List devices with filters and pagination")
  @GetMapping
  public ResponseEntity<List<DeviceResponse>> list(
    @RequestParam Optional<String> brand,
    @RequestParam Optional<String> state,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size
  ) {
    size = Math.max(1, Math.min(size, 200));
    page = Math.max(0, page);

    Optional<DeviceState> st = state.map(this::parseState);
    var pr = service.listPaged(brand, st, page, size);
    var body = pr.items().stream().map(DeviceMapper::toResponse).toList();

    return ResponseEntity.ok()
      .header("X-Total-Count", String.valueOf(pr.total()))
      .header("Cache-Control", "no-store")
      .body(body);
  }
  @Operation(summary = "Replace a device (PUT)")
  @PutMapping("/{id}")
  public ResponseEntity<DeviceResponse> updatePut(
    @PathVariable UUID id,
    @RequestHeader(value = "If-Match", required = false) String ifMatch,
    @Valid @RequestBody DeviceRequest req
  ) {
    assertIfMatch(id, ifMatch);
    var updated = service.updatePut(new UpdateDevicePutCommand(id, req.name(), req.brand(), req.state()));
    return ResponseEntity.ok()
      .eTag(EtagUtil.etagFor(updated))
      .body(DeviceMapper.toResponse(updated));
  }
  @Operation(summary = "Partially update a device (PATCH)")
  @PatchMapping("/{id}")
  public ResponseEntity<DeviceResponse> updatePatch(
    @PathVariable UUID id,
    @RequestHeader(value = "If-Match", required = false) String ifMatch,
    @RequestBody DevicePatchRequest req
  ) {
    assertIfMatch(id, ifMatch);
    var updated = service.updatePatch(new UpdateDevicePatchCommand(
      id,
      Optional.ofNullable(req.name()),
      Optional.ofNullable(req.brand()),
      Optional.ofNullable(req.state())
    ));
    return ResponseEntity.ok()
      .eTag(EtagUtil.etagFor(updated))
      .body(DeviceMapper.toResponse(updated));
  }
  @Operation(summary = "Delete a device")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
    @PathVariable UUID id,
    @RequestHeader(value = "If-Match", required = false) String ifMatch
  ) {
    assertIfMatch(id, ifMatch);
    service.delete(id);
    return ResponseEntity.noContent().build();
  }

  private DeviceState parseState(String raw) {
    try {
      return DeviceState.valueOf(raw.trim().toUpperCase(Locale.ROOT));
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid state: " + raw);
    }
  }

  private void assertIfMatch(UUID id, String ifMatchHeader) {
    if (ifMatchHeader == null || ifMatchHeader.isBlank()) return;
    var current = service.get(id);
    var currentEtag = EtagUtil.etagFor(current);
    if (!ifMatchHeader.equals(currentEtag)) {
      throw new PreconditionFailed("If-Match does not match current ETag");
    }
  }
}
