package com.ivanminyaev.anonymous_chat_bot.service;

import com.ivanminyaev.anonymous_chat_bot.service.matchmaking.MatchmakingStorage;
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
    MatchmakingStorage matchmakingStorage;
    MessageSender messageSender;

    private static final String START = "\uD83D\uDC4B Добро пожаловать в Анончатти, %s!";
    private static final String STOP = "Вы завершили диалог.";
    private static final String STOP_PARTNER = "Ваш собеседник завершил диалог.";

    public void start(Message message) throws TelegramApiException {
        final long chatId = message.getChatId();
        final String firstName = message.getChat().getFirstName();
        final String text = String.format(START, firstName);
        final int messageId = message.getMessageId();

        messageSender.send(chatId, text, messageId, searchMarkup());
    }

    public void stop(Message message) throws TelegramApiException {
        final long chatId = message.getChatId();
        final long partnerChatId = matchmakingStorage.getPartnerChatId(chatId);

        matchmakingStorage.stopDialog(chatId);

        final int messageId = message.getMessageId();
        messageSender.send(chatId, STOP, messageId, searchMarkup());
        messageSender.send(partnerChatId, STOP_PARTNER, searchMarkup());
    }
}
