package com.ivanminyaev.anonymous_chat_bot.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telegram")
public record TelegramProperties(@NotNull @NotBlank String username, @NotNull @NotBlank String token) {

}
