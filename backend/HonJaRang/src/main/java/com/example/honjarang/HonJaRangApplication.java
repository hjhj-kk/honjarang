package com.example.honjarang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class HonJaRangApplication {

	public static void main(String[] args) {
		SpringApplication.run(HonJaRangApplication.class, args);
	}

}