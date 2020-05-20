package com.ashok.app;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.ashok.app.service.CollectionService;

@SpringBootApplication
public class Main {

	public static void main(String[] args) {

		ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);
		context.getBean(CollectionService.class).startProcess();
		
	}

}
