package com.ivanminyaev.anonymous_chat_bot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telegram")
public record TelegramProperties(String username, String token) {

}
