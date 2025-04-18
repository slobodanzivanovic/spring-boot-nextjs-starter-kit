package com.slobodanzivanovic.example.core;

import com.slobodanzivanovic.example.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.slobodanzivanovic.example")
public class CoreApplication {

	private static final String CLASSNAME = CoreApplication.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASSNAME);

	public static void main(String[] args) {
		SpringApplication.run(CoreApplication.class, args);
		LOGGER.info("{} started", CLASSNAME);
		Test.test();
	}

}
