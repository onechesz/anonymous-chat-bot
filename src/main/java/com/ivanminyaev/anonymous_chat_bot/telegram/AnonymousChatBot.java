package com.ivanminyaev.anonymous_chat_bot.telegram;

import com.ivanminyaev.anonymous_chat_bot.config.TelegramProperties;
import com.ivanminyaev.anonymous_chat_bot.handler.router.UpdateRouter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Component
public class AnonymousChatBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    TelegramProperties telegramProperties;
    UpdateRouter updateRouter;

    @Override
    public String getBotToken() {
        final String token = telegramProperties.token();

        return token;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        try {
            updateRouter.consume(update);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
