package com.kttt.webbanve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebBanVeApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebBanVeApplication.class, args);
	}

}
