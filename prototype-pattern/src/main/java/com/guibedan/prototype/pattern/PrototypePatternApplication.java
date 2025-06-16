package com.guibedan.prototype.pattern;

import java.util.UUID;

public class PrototypePatternApplication {

	public static void main(String[] args) {
		System.out.println("Welcome to Prototype Pattern");

		var user1 = new User();
		user1.setId(UUID.randomUUID().toString());
		user1.setEmail("teste@email.com");
		user1.setPassword("123456");
		user1.setUsername("myTestUser");

		var userClone = user1.clone();

		System.out.println(user1);
		System.out.println(userClone);

	}

}
