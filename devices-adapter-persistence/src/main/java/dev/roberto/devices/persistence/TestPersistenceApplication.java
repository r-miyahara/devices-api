package dev.roberto.devices.persistence;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Mini aplicação apenas para os testes de JPA.
 * Fica no pacote PAI (dev.roberto.devices.persistence) para escanear
 * os subpacotes: .entity, .repository e .adapter.
 */
@SpringBootApplication
@EntityScan("dev.roberto.devices.persistence.entity")
@EnableJpaRepositories("dev.roberto.devices.persistence.repository")
class TestPersistenceApplication { }
