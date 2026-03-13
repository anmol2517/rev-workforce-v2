package com.revworkforce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ComponentScan(basePackages = "com.revworkforce")
@SpringBootApplication
@EnableTransactionManagement
public class RevWorkforceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RevWorkforceApplication.class, args);
	}
}
