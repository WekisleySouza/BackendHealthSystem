package com.project.healthsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class HealthsystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthsystemApplication.class, args);
	}

}
