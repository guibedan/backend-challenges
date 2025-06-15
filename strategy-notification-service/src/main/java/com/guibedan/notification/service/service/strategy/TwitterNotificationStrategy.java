package com.guibedan.notification.service.service.strategy;

import org.springframework.stereotype.Component;

import com.guibedan.notification.service.service.NotificationStrategy;

import lombok.extern.slf4j.Slf4j;

@Component("TWITTER")
@Slf4j
public class TwitterNotificationStrategy implements NotificationStrategy {
	@Override
	public void sendNotification(String destination, String message) {
		log.info("Notificação [{}] enviada para o Twitter [{}]", message, destination);
	}
}
