package dev.roberto.devices.web;

import dev.roberto.devices.domain.model.Device;
import dev.roberto.devices.domain.model.DeviceState;
import dev.roberto.devices.usecase.DeviceService;
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

  public DeviceController(DeviceService service) {
    this.service = service;
  }

  // POST /devices
  @PostMapping
  public ResponseEntity<DeviceResponse> create(@Valid @RequestBody DeviceRequest req) {
    var created = service.create(new CreateDeviceCommand(req.name(), req.brand(), req.state()));
    return ResponseEntity.created(URI.create("/devices/" + created.id()))
      .body(DeviceMapper.toResponse(created));
  }

  // GET /devices/{id}
  @GetMapping("/{id}")
  public DeviceResponse get(@PathVariable UUID id) {
    return DeviceMapper.toResponse(service.get(id));
  }

  // GET /devices?brand=&state=
  @GetMapping
  public List<DeviceResponse> list(
    @RequestParam Optional<String> brand,
    @RequestParam Optional<String> state
  ) {
    List<Device> base;
    if (brand.isEmpty() && state.isEmpty()) {
      base = service.listAll();
    } else if (brand.isPresent() && state.isEmpty()) {
      base = service.listByBrand(brand.get());
    } else if (state.isPresent() && brand.isEmpty()) {
      base = service.listByState(parseState(state.get()));
    } else {
      // ambos presentes → interseção simples
      var byBrand = service.listByBrand(brand.get());
      var st = parseState(state.get());
      base = byBrand.stream().filter(d -> d.state() == st).collect(Collectors.toList());
    }
    return base.stream().map(DeviceMapper::toResponse).toList();
  }

  // PUT /devices/{id}
  @PutMapping("/{id}")
  public DeviceResponse updatePut(@PathVariable UUID id, @Valid @RequestBody DeviceRequest req) {
    var updated = service.updatePut(new UpdateDevicePutCommand(id, req.name(), req.brand(), req.state()));
    return DeviceMapper.toResponse(updated);
  }

  // PATCH /devices/{id}
  @PatchMapping("/{id}")
  public DeviceResponse updatePatch(@PathVariable UUID id, @RequestBody DevicePatchRequest req) {
    var updated = service.updatePatch(new UpdateDevicePatchCommand(
      id,
      Optional.ofNullable(req.name()),
      Optional.ofNullable(req.brand()),
      Optional.ofNullable(req.state())
    ));
    return DeviceMapper.toResponse(updated);
  }

  // DELETE /devices/{id}
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
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
}
