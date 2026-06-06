package com.ivanminyaev.anonymous_chat_bot.service;

import com.ivanminyaev.anonymous_chat_bot.telegram.MessageSender;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.ivanminyaev.anonymous_chat_bot.keyboard.ReplyKeyboardTemplate.searchMarkup;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Service
public class UserService {
    MessageSender messageSender;

    private static final String START = "\uD83D\uDC4B Добро пожаловать в Анончатти, %s!";

    public void start(Message message) throws TelegramApiException {
        final long chatId = message.getChatId();
        final String firstName = message.getChat().getFirstName();
        final String text = String.format(START, firstName);
        final int messageId = message.getMessageId();

        messageSender.send(chatId, text, messageId, searchMarkup());
    }
}
