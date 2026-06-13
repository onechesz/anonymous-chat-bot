package com.ivanminyaev.anonymous_chat_bot.handler;

import com.ivanminyaev.anonymous_chat_bot.service.MatchmakingService;
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
public class CommandHandler implements Handler {
    UserService userService;
    MatchmakingService matchmakingService;

    private static final String START = "/start";
    private static final String STOP = "/stop";

    public void execute(Message message) throws TelegramApiException {
        final String text = message.getText();

        switch (text) {
            case START -> userService.start(message);
            case STOP -> matchmakingService.stopDialog(message);
        }
    }
}
