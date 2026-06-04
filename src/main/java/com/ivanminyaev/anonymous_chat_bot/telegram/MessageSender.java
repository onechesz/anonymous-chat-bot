package com.ivanminyaev.anonymous_chat_bot.telegram;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Component
public class MessageSender {
    TelegramClient telegramClient;

    public void send(SendMessage sendMessage) throws TelegramApiException {
        telegramClient.execute(sendMessage);
    }
}
