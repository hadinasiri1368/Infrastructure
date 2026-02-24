package com.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.infrastructure.domain.authentication.repository")
public class InfrastructureApplication {

	public static void main(String[] args) {
		SpringApplication.run(InfrastructureApplication.class, args);
	}

}
