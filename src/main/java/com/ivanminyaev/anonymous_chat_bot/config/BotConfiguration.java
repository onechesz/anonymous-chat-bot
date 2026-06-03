package com.ivanminyaev.anonymous_chat_bot.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Configuration
public class BotConfiguration {
    TelegramProperties telegramProperties;

    @Bean(value = "telegramClient")
    public TelegramClient telegramClient() {
        final TelegramClient telegramClient = new OkHttpTelegramClient(telegramProperties.token());

        return telegramClient;
    }
}
