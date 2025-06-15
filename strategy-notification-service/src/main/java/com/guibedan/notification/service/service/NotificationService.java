package com.guibedan.notification.service.service;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.guibedan.notification.service.exceptions.BusinessException;

@Service
public class NotificationService {

	private final Map<String, NotificationStrategy> mapStrategy;

	public NotificationService(Map<String, NotificationStrategy> mapStrategy) {
		this.mapStrategy = mapStrategy;
	}

	public void notify(String channel, String destination, String message) {
		NotificationStrategy strategy = mapStrategy.get(channel);
		if (strategy == null) {
			throw new BusinessException("Not supported channel!", HttpStatus.BAD_REQUEST.value());
		}
		strategy.sendNotification(destination, message);
	}

}
