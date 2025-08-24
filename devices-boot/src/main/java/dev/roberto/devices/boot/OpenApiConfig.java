package dev.roberto.devices.boot;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI devicesOpenAPI() {
    return new OpenAPI()
      .info(new Info()
        .title("Devices API")
        .version("0.1.0")
        .description("API para gestão de dispositivos (Clean Architecture + SOLID)"));
  }
}
