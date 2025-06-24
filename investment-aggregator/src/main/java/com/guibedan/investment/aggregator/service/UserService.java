package com.guibedan.investment.aggregator.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.guibedan.investment.aggregator.controller.dto.CreateUserDto;
import com.guibedan.investment.aggregator.controller.dto.UpdateDto;
import com.guibedan.investment.aggregator.controller.dto.UserDto;
import com.guibedan.investment.aggregator.entities.User;
import com.guibedan.investment.aggregator.exceptions.NotFoundException;
import com.guibedan.investment.aggregator.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public String createUser(CreateUserDto newUser) {
		User user = new User();
		BeanUtils.copyProperties(newUser, user);
		userRepository.save(user);
		return user.getUserId().toString();
	}

	public UserDto getUserById(String id) {
		UUID uuid = UUID.fromString(id);
		User user = userRepository.findById(uuid).orElseThrow(() -> new NotFoundException("User not found"));
		return new UserDto(user.getUsername(), user.getEmail(), user.getCreatedAt());
	}

	public List<UserDto> getUsers() {
		var users = new ArrayList<UserDto>();
		userRepository.findAll().forEach(x -> users.add(new UserDto(x.getUsername(), x.getEmail(), x.getCreatedAt())));
		return users;
	}

	public void updateUser(String id, UpdateDto updateDto) {
		UUID uuid = UUID.fromString(id);
		User user = userRepository.findById(uuid).orElseThrow(() -> new NotFoundException("User not found"));
		if (!updateDto.username().isEmpty())
			user.setUsername(updateDto.username());
		if (!updateDto.password().isEmpty())
			user.setPassword(updateDto.password());
		userRepository.save(user);
	}

	public void deleteUser(String id) {
		UUID uuid = UUID.fromString(id);
		if (!userRepository.existsById(uuid)) {
			throw new NotFoundException("User not found");
		}
		userRepository.deleteById(uuid);
	}

}
