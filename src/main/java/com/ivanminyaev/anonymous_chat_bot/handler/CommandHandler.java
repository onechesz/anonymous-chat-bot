package com.ivanminyaev.anonymous_chat_bot.handler;

import com.ivanminyaev.anonymous_chat_bot.service.UserService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Component
public class CommandHandler {
    UserService userService;

    private static final String START = "/start";

    public void execute(Message message) throws TelegramApiException {
        final String text = message.getText();

        switch (text) {
            case START -> userService.start(message);
        }
    }
}
