package dev.roberto.devices.boot;

import dev.roberto.devices.domain.port.DeviceRepository;
import dev.roberto.devices.domain.time.TimeProvider;
import dev.roberto.devices.usecase.DeviceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;

@Configuration
public class AppConfig {

  @Bean
  TimeProvider timeProvider() {
    // Implementação simples, testável (via bean override em testes)
    return Instant::now;
  }

  @Bean
  DeviceService deviceService(DeviceRepository repository, TimeProvider timeProvider) {
    return new DeviceService(repository, timeProvider);
  }
}
