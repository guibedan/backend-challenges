package com.guibedan.investment.aggregator.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.guibedan.investment.aggregator.controller.dto.CreateUserDto;
import com.guibedan.investment.aggregator.controller.dto.UpdateDto;
import com.guibedan.investment.aggregator.entities.User;
import com.guibedan.investment.aggregator.exceptions.NotFoundException;
import com.guibedan.investment.aggregator.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	@Captor
	private ArgumentCaptor<User> userCaptor;

	@Captor
	private ArgumentCaptor<UUID> UUIDCaptor;

	@Nested
	class CreateUser {

		@Test
		void shouldCreateAUserWithSuccess() {
			// ARRANGE
			var input = new CreateUserDto("username", "email@email.com", "password");
			doAnswer(invocation -> {
				User userToSave = invocation.getArgument(0);
				userToSave.setUserId(UUID.randomUUID());
				return userToSave;
			}).when(userRepository).save(userCaptor.capture());

			// ACT
			var output = userService.createUser(input);

			// ASSERT
			assertNotNull(output);

			var userCaptured = userCaptor.getValue();

			assertEquals(input.username(), userCaptured.getUsername());
			assertEquals(input.email(), userCaptured.getEmail());
			assertEquals(input.password(), userCaptured.getPassword());
		}

		@Test
		void shouldThrowExceptionWhenErrorOccurs() {
			// ARRANGE
			doThrow(new RuntimeException()).when(userRepository).save(any());
			var input = new CreateUserDto("username", "email@email.com", "password");

			// ACT + ASSERT
			assertThrows(RuntimeException.class, () -> userService.createUser(input));
		}

	}

	@Nested
	class GetUserById {

		@Test
		void shouldGetByIdWithSuccess() {

			// ARRANGE
			var user = new User(UUID.randomUUID(), "username", "email@email.com", "password", Instant.now(), null);
			doReturn(Optional.of(user)).when(userRepository).findById(UUIDCaptor.capture());

			// ACT
			var output = userService.getUserById(user.getUserId().toString());

			// ASSERT
			assertDoesNotThrow(() -> userRepository.findById(user.getUserId()));
			assertEquals(user.getUsername(), output.username());
		}

		@Test
		void shouldGetByIdNotFound() {

			// ARRANGE
			var userId = UUID.randomUUID();
			when(userRepository.findById(userId)).thenReturn(Optional.empty());

			// ACT + ASSERT
			var thrown = assertThrows(NotFoundException.class, () -> userService.getUserById(userId.toString()));
			assertEquals("User not found", thrown.getMessage());
		}

	}

	@Nested
	class GetUsers {

		@Test
		void shouldReturnAllUsersWithSuccess() {
			// ARRANGE
			var user = new User(UUID.randomUUID(), "username", "email@email.com", "password", Instant.now(), null);
			doReturn(List.of(user)).when(userRepository).findAll();

			// ACT
			var list = userService.getUsers();

			// ASSERT
			assertFalse(list.isEmpty());

			assertEquals(user.getUsername(), list.get(0).username());
			assertEquals(user.getEmail(), list.get(0).email());
		}

		@Test
		void shouldReturnEmptyListWithSuccess() {
			// ARRANGE
			doReturn(Collections.emptyList()).when(userRepository).findAll();

			// ACT
			var list = userService.getUsers();

			// ASSERT
			assertTrue(list.isEmpty());
		}

	}

	@Nested
	class DeleteUser {

		@Test
		void shouldDeleteUserWithSuccess() {

			// ARRANGE
			var userId = UUID.randomUUID();
			when(userRepository.existsById(userId)).thenReturn(true);

			// ACT + ASSERT
			assertDoesNotThrow(() -> userService.deleteUser(userId.toString()));

			verify(userRepository, times(1)).deleteById(UUIDCaptor.capture());
			assertEquals(userId, UUIDCaptor.getValue());

			verify(userRepository, times(1)).existsById(userId);
		}

		@Test
		void shouldDeleteUserNotFound() {

			// ARRANGE
			var userId = UUID.randomUUID();
			when(userRepository.existsById(userId)).thenReturn(false);

			// ACT + ASSERT
			var thrown = assertThrows(NotFoundException.class, () -> userService.deleteUser(userId.toString()));
			assertEquals("User not found", thrown.getMessage());

			verify(userRepository, times(1)).existsById(userId);
			verify(userRepository, times(0)).deleteById(userId);
		}

	}

	@Nested
	class UpdateUser {

		@Test
		void shouldUpdateUserByIdWhenUserExistsAndUsernameAndPasswordIsFilled() {

			// Arrange
			var updateUserDto = new UpdateDto("newUsername", "newPassword");
			var user = new User(UUID.randomUUID(), "username", "email@email.com", "password", Instant.now(), null);
			doReturn(Optional.of(user)).when(userRepository).findById(UUIDCaptor.capture());
			doReturn(user).when(userRepository).save(userCaptor.capture());

			// Act + Assert
			assertDoesNotThrow(() -> userService.updateUser(user.getUserId().toString(), updateUserDto));

			// Assert
			var userCaptured = userCaptor.getValue();

			assertEquals(updateUserDto.username(), userCaptured.getUsername());
			assertEquals(updateUserDto.password(), userCaptured.getPassword());

			verify(userRepository, times(1)).save(user);
		}

		@Test
		void shouldNotUpdateUserWhenUserNotExists() {
			// ARRANGE
			var userId = UUID.randomUUID();
			when(userRepository.findById(userId)).thenReturn(Optional.empty());

			// ACT + ASSERT
			var thrown = assertThrows(NotFoundException.class,
					() -> userService.updateUser(userId.toString(), any(UpdateDto.class)));
			assertEquals("User not found", thrown.getMessage());
		}

	}

}