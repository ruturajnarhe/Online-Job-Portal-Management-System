package com.jsp.ojpms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OjpmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(OjpmsApplication.class, args);
	}

}