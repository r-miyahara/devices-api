package dev.roberto.devices.domain.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "dev.roberto.devices")
@EntityScan("dev.roberto.devices.domain.persistence.entity")
@EnableJpaRepositories("dev.roberto.devices.domain.persistence.repository")
public class DevicesApplication {
  public static void main(String[] args) {
    SpringApplication.run(DevicesApplication.class, args);
  }
}
