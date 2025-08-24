package dev.roberto.devices.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "dev.roberto.devices")
public class DevicesApplication {
  public static void main(String[] args) {
    SpringApplication.run(DevicesApplication.class, args);
  }
}
