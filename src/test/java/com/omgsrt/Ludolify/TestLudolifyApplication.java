package com.omgsrt.Ludolify;

import org.springframework.boot.SpringApplication;

public class TestLudolifyApplication {

	public static void main(String[] args) {
		SpringApplication.from(LudolifyApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
