package com.ivanminyaev.anonymous_chat_bot.service;

import com.ivanminyaev.anonymous_chat_bot.telegram.MessageSender;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.ivanminyaev.anonymous_chat_bot.keyboard.ReplyKeyboardTemplate.searchMarkup;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Service
public class UserService {
    MessageSender messageSender;

    public void start(Message message) throws TelegramApiException {
        final long chatId = message.getChatId();
        final String firstName = message.getChat().getFirstName();
        final String text = String.format("\uD83D\uDC4B Добро пожаловать в Анончатти, %s!", firstName);

        final SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(searchMarkup()).build();
        messageSender.send(sendMessage);
    }
}
