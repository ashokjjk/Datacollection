package com.ashok.app;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.ashok.app.service.CollectionService;

@SpringBootApplication
public class Main {
	static String ipAddress;

	public static String getIpAddress() {
		return ipAddress;
	}

	public static void main(String[] args) throws BeansException, MqttException {
		ipAddress = args[0];
		ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);
		context.getBean(CollectionService.class).startProcess();

	}

}
