package dev.roberto.devices.web;

import dev.roberto.devices.domain.model.Device;
import dev.roberto.devices.domain.model.DeviceState;
import dev.roberto.devices.usecase.DeviceService;
import dev.roberto.devices.usecase.PageResult;
import dev.roberto.devices.usecase.command.CreateDeviceCommand;
import dev.roberto.devices.usecase.command.UpdateDevicePatchCommand;
import dev.roberto.devices.usecase.command.UpdateDevicePutCommand;
import dev.roberto.devices.web.dto.DevicePatchRequest;
import dev.roberto.devices.web.dto.DeviceRequest;
import dev.roberto.devices.web.dto.DeviceResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/devices")
public class DeviceController {

  private final DeviceService service;
  private final IdempotencyService idempotencyService;
  public DeviceController(DeviceService service, IdempotencyService idempotencyService) {
    this.service = service;
    this.idempotencyService = idempotencyService;
  }


  // POST /devices
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


  @GetMapping("/{id}")
  public ResponseEntity<DeviceResponse> get(
    @PathVariable UUID id,
    @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch
  ) {
    var d = service.get(id);
    var etag = EtagUtil.etagFor(d);
    if (ifNoneMatch != null && ifNoneMatch.equals(etag)) {
      return ResponseEntity.status(304).eTag(etag).build();
    }
    return ResponseEntity.ok().eTag(etag).body(DeviceMapper.toResponse(d));
  }


  @GetMapping
  public ResponseEntity<List<DeviceResponse>> list(
    @RequestParam Optional<String> brand,
    @RequestParam Optional<String> state,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size
  ) {
    size = Math.max(1, Math.min(size, 200));
    page = Math.max(0, page);

    Optional<DeviceState> st = state.map(s -> parseState(s));
    PageResult<Device> pr = service.listPaged(brand, st, page, size);

    var body = pr.items().stream().map(DeviceMapper::toResponse).toList();
    return ResponseEntity.ok()
      .header("X-Total-Count", String.valueOf(pr.total()))
      .body(body);
  }

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

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
    @PathVariable UUID id,
    @RequestHeader(value = "If-Match", required = false) String ifMatch
  ) {
    assertIfMatch(id, ifMatch);
    service.delete(id);
    return ResponseEntity.noContent().build();
  }

  private static DeviceState parseState(String raw) {
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
      throw new IllegalArgumentException("Precondition failed (If-Match does not match current ETag)");
    }
  }
}
