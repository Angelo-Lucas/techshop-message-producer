package com.techshop.techshopmessageproducer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TechshopMessageProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TechshopMessageProducerApplication.class, args);
	}

}
