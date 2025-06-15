package com.guibedan.notification.service.service.strategy;

import org.springframework.stereotype.Component;

import com.guibedan.notification.service.service.NotificationStrategy;

import lombok.extern.slf4j.Slf4j;

@Component("DISCORD")
@Slf4j
public class DiscordNotificationStrategy implements NotificationStrategy {
	@Override
	public void sendNotification(String destination, String message) {
		log.info("Notificação [{}] enviada para o Discord [{}]", message, destination);
	}
}
