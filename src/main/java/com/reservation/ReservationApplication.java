package com.reservation;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@OpenAPIDefinition(
        info = @Info(
                title = "Reservation API",
                version = "1.0",
                description = "Comprehensive REST API for managing reservation lifecycles, including booking, payments, and cancellations."
        ),
        servers = {
                @io.swagger.v3.oas.annotations.servers.Server(
                        url = "http://localhost:8080",
                        description = "Local server"
                )
        }
)
@SpringBootApplication
@EnableScheduling
public class ReservationApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationApplication.class, args);
	}
}
