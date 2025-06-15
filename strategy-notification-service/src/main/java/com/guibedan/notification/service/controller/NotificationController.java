package com.guibedan.notification.service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.guibedan.notification.service.controller.dto.NotificationRequest;
import com.guibedan.notification.service.service.NotificationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/notification")
public class NotificationController {

	private final NotificationService notificationService;

	public NotificationController(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@PostMapping
	public ResponseEntity<Void> sendNotification(@RequestBody @Valid NotificationRequest request) {
		notificationService.notify(request.channel(), request.destination(), request.message());

		return ResponseEntity.accepted().build();
	}

}
