package com.guibedan.investment.aggregator.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.guibedan.investment.aggregator.controller.dto.CreateUserDto;
import com.guibedan.investment.aggregator.controller.dto.UpdateDto;
import com.guibedan.investment.aggregator.controller.dto.UserDto;
import com.guibedan.investment.aggregator.service.UserService;

@RestController
@RequestMapping("/v1/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping
	public ResponseEntity<?> addUser(@RequestBody CreateUserDto user) {
		String userId = userService.createUser(user);
		return ResponseEntity.created(
				ServletUriComponentsBuilder.fromCurrentRequest().path("/{userId}").buildAndExpand(userId).toUri())
				.build();
	}

	@GetMapping("/{userId}")
	public ResponseEntity<UserDto> getUserById(@PathVariable String userId) {
		var user = userService.getUserById(userId);
		return ResponseEntity.ok(user);
	}

	@GetMapping()
	public ResponseEntity<List<UserDto>> getUsers() {
		var user = userService.getUsers();
		return ResponseEntity.ok(user);
	}

	@PutMapping("/{userId}")
	public ResponseEntity<Void> updateUser(@PathVariable String userId, @RequestBody UpdateDto user) {
		userService.updateUser(userId, user);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<Void> deleteUserById(@PathVariable String userId) {
		userService.deleteUser(userId);
		return ResponseEntity.noContent().build();
	}

}
