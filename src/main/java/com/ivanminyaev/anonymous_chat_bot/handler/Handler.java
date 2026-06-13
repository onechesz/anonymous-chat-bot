package com.ivanminyaev.anonymous_chat_bot.handler;

import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface Handler {
    void execute(Message message) throws TelegramApiException;
}
