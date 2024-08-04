package com.example.lock;

import org.springframework.boot.SpringApplication;

public class TestLockApplication {

	public static void main(String[] args) {
		SpringApplication.from(LockApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
