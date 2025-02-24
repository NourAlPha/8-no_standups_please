package com.example.MiniProject1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.*")
public class MiniProject1Application {

	public static void main(String[] args) {
		SpringApplication.run(MiniProject1Application.class, args);
	}

}
