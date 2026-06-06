package com.ivanminyaev.anonymous_chat_bot.telegram;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
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

    public void send(long chatId, String text) throws TelegramApiException {
        final SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text).build();

        this.send(sendMessage);
    }

    public void send(long chatId, String text, int replyToMessageId) throws TelegramApiException {
        final SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyToMessageId(replyToMessageId).build();

        this.send(sendMessage);
    }

    public void send(long chatId, String text, ReplyKeyboard replyKeyboard) throws TelegramApiException {
        final SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(replyKeyboard).build();

        this.send(sendMessage);
    }

    public void send(long chatId, String text, int replyToMessageId, ReplyKeyboard replyKeyboard) throws TelegramApiException {
        final SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyToMessageId(replyToMessageId)
                .replyMarkup(replyKeyboard).build();

        this.send(sendMessage);
    }
}
