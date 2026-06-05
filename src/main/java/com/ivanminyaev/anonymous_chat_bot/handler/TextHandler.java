package com.ivanminyaev.anonymous_chat_bot.handler;

import com.ivanminyaev.anonymous_chat_bot.keyboard.Command;
import com.ivanminyaev.anonymous_chat_bot.service.ChatService;
import com.ivanminyaev.anonymous_chat_bot.service.MatchmakingService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Component
public class TextHandler {
    MatchmakingService matchmakingService;
    ChatService chatService;

    public void execute(Message message) throws TelegramApiException {
        final String text = message.getText();
        final Command command = Command.of(text);

        switch (command) {
            case SEARCH -> matchmakingService.search(message);
            case STOP -> matchmakingService.stop(message);
            case null, default -> chatService.send(message);
        }
    }
}
