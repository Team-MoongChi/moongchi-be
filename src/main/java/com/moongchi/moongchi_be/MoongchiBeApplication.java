package com.moongchi.moongchi_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MoongchiBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoongchiBeApplication.class, args);
	}

}
