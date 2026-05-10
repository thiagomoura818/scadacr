package com.scada.ScadaCR;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ScadaCrApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScadaCrApplication.class, args);
	}

}
