package com.ivanminyaev.anonymous_chat_bot.service;

import com.ivanminyaev.anonymous_chat_bot.dao.entity.UserEntity;
import com.ivanminyaev.anonymous_chat_bot.dao.repository.UserRepository;
import com.ivanminyaev.anonymous_chat_bot.telegram.MessageSender;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;

import static com.ivanminyaev.anonymous_chat_bot.keyboard.ReplyKeyboardTemplate.searchMarkup;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Service
public class UserService {
    UserRepository userRepository;
    MessageSender messageSender;

    private static final String START = "<i>Добро пожаловать в <b>Анончатти</b>, %s!</i>";

    public void start(Message message) throws TelegramApiException {
        final User user = message.getFrom();
        final String firstName = user.getFirstName();

        final long chatId = message.getChatId();
        final String text = String.format(START, firstName);
        final int messageId = message.getMessageId();

        messageSender.send(chatId, text, messageId, searchMarkup());
    }

    @Transactional
    public void updateOrRegister(User user) {
        final LocalDateTime now = LocalDateTime.now();

        final long telegramId = user.getId();
        UserEntity userEntity = userRepository.findByTelegramId(telegramId);

        if (userEntity == null) {
            userEntity = new UserEntity();

            userEntity.setTelegramId(telegramId);
            userEntity.setBot(user.getIsBot());
            userEntity.setRegisteredAt(now);
        }

        userEntity.setUsername(user.getUserName());
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setLastSeenAt(now);
        userRepository.save(userEntity);
    }
}
