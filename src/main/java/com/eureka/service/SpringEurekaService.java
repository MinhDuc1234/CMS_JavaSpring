package com.eureka.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringEurekaService {

	public static void main(String[] args) {
		SpringApplication.run(SpringEurekaService.class, args);
	}

}
