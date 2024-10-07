package com.nextPick;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class nextPickApplication {

	public static void main(String[] args) {
		SpringApplication.run(nextPickApplication.class, args);
	}
}
