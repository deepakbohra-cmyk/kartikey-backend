package com.kartikey.kartikey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class KartikeyApplication {

	public static void main(String[] args) {
		SpringApplication.run(KartikeyApplication.class, args);
	}

}
