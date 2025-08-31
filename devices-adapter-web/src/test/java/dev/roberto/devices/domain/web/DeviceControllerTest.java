package dev.roberto.devices.domain.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.roberto.devices.domain.model.Device;
import dev.roberto.devices.domain.model.DeviceState;
import dev.roberto.devices.domain.usecase.DeviceService;
import dev.roberto.devices.domain.usecase.PageResult;
import dev.roberto.devices.domain.usecase.command.UpdateDevicePutCommand;
import dev.roberto.devices.domain.usecase.exception.DomainRuleViolationException;
import dev.roberto.devices.domain.usecase.exception.NotFoundException;
import dev.roberto.devices.domain.web.dto.DeviceRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DeviceController.class)
class DeviceControllerTest {

  @Autowired MockMvc mvc;
  @Autowired ObjectMapper om;

  @MockBean
  DeviceService service;
  @MockBean IdempotencyService idempotencyService;

  @Test
  void create_shouldReturn201_location_andEtag() throws Exception {
    var id = UUID.randomUUID();
    var d = new Device(id, "WS-01", "Lenovo", DeviceState.AVAILABLE, Instant.parse("2025-01-01T00:00:00Z"));
    org.mockito.Mockito.when(service.create(any())).thenReturn(d);

    var body = new DeviceRequest("WS-01", "Lenovo", DeviceState.AVAILABLE);

    mvc.perform(post("/devices")
        .contentType(MediaType.APPLICATION_JSON)
        .content(om.writeValueAsBytes(body)))
      .andExpect(status().isCreated())
      .andExpect(header().string("Location", "/devices/" + id))
      .andExpect(header().exists("ETag"))
      .andExpect(jsonPath("$.id", is(id.toString())))
      .andExpect(jsonPath("$.name", is("WS-01")))
      .andExpect(jsonPath("$.brand", is("Lenovo")))
      .andExpect(jsonPath("$.state", is("AVAILABLE")));
  }

  @Test
  void get_shouldReturn404_whenNotFound() throws Exception {
    var id = UUID.randomUUID();
    org.mockito.Mockito.when(service.get(id)).thenThrow(new NotFoundException("not found"));

    mvc.perform(get("/devices/{id}", id))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status", is(404)))
      .andExpect(jsonPath("$.error", containsString("Not")))
      .andExpect(jsonPath("$.path", is("/devices/" + id)));
  }

  @Test
  void get_shouldReturn304_whenIfNoneMatchMatches() throws Exception {
    var id = UUID.randomUUID();
    var d = new Device(id, "WS-01", "Lenovo", DeviceState.AVAILABLE, Instant.parse("2025-01-01T00:00:00Z"));
    org.mockito.Mockito.when(service.get(id)).thenReturn(d);

    var etag = EtagUtil.etagFor(d);

    mvc.perform(get("/devices/{id}", id).header("If-None-Match", etag))
      .andExpect(status().isNotModified())
      .andExpect(header().string("ETag", etag));
  }

  @Test
  void list_byState_shouldReturnFiltered_andTotalCountHeader() throws Exception {
    var a = new Device(UUID.randomUUID(), "A", "Apple", DeviceState.AVAILABLE, Instant.now());
    var page = new PageResult<>(List.of(a), 1L, 0, 20);


    org.mockito.Mockito.when(
      service.listPaged(eq(Optional.empty()), eq(Optional.of(DeviceState.AVAILABLE)), anyInt(), anyInt())
    ).thenReturn(page);

    mvc.perform(get("/devices").param("state", "available"))
      .andExpect(status().isOk())
      .andExpect(header().string("X-Total-Count", "1"))
      .andExpect(jsonPath("$", hasSize(1)))
      .andExpect(jsonPath("$[0].name", is("A")))
      .andExpect(jsonPath("$[0].state", is("AVAILABLE")));
  }

  @Test
  void updatePut_shouldReturn422_onDomainViolation() throws Exception {
    var id = UUID.randomUUID();
    org.mockito.Mockito.when(service.updatePut(any(UpdateDevicePutCommand.class)))
      .thenThrow(new DomainRuleViolationException("rule"));

    var body = new DeviceRequest("WS-02", "Lenovo", DeviceState.IN_USE);

    mvc.perform(put("/devices/{id}", id)
        .contentType(MediaType.APPLICATION_JSON)
        .content(om.writeValueAsBytes(body)))
      .andExpect(status().isUnprocessableEntity())
      .andExpect(jsonPath("$.status", is(422)));
  }

  @Test
  void delete_shouldReturn204() throws Exception {
    var id = UUID.randomUUID();
    mvc.perform(delete("/devices/{id}", id))
      .andExpect(status().isNoContent());
  }
}
