package com.ivanminyaev.anonymous_chat_bot.handler.router;

import com.ivanminyaev.anonymous_chat_bot.dao.entity.enumeration.ContentType;
import com.ivanminyaev.anonymous_chat_bot.handler.CommandHandler;
import com.ivanminyaev.anonymous_chat_bot.handler.ContentHandler;
import com.ivanminyaev.anonymous_chat_bot.handler.TextHandler;
import com.ivanminyaev.anonymous_chat_bot.service.UserService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Component
public class UpdateRouter {
    UserService userService;
    CommandHandler commandHandler;
    TextHandler textHandler;
    ContentHandler contentHandler;

    public void consume(Update update) throws TelegramApiException {
        if (!update.hasMessage()) {
            return;
        }

        userService.updateOrRegister(update.getMessage().getFrom());

        final Message message = update.getMessage();
        final ContentType contentType = ContentType.of(message);

        if (message.hasText()) {
            final String text = message.getText();

            if (!StringUtils.hasText(text)) {
                return;
            }

            if (text.startsWith("/")) {
                commandHandler.execute(message);
            } else {
                textHandler.execute(message);
            }
        } else if (contentType != null && !ContentType.TEXT.equals(contentType)) {
            contentHandler.execute(message);
        }
    }
}
