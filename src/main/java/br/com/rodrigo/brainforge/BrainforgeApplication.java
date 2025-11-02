package br.com.rodrigo.brainforge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BrainforgeApplication {

	public static void main(String[] args) {
		SpringApplication.run(BrainforgeApplication.class, args);
	}

}
