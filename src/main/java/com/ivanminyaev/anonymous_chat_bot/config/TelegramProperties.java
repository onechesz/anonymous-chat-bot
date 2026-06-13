package com.ivanminyaev.anonymous_chat_bot.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "telegram")
@Validated
public record TelegramProperties(@NotNull @NotBlank String username, @NotNull @NotBlank String token) {

}
