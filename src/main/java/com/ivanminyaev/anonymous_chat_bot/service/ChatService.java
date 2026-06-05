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
import static com.ivanminyaev.anonymous_chat_bot.keyboard.ReplyKeyboardTemplate.stopMarkup;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Service
public class ChatService {
    MatchmakingStorage matchmakingStorage;
    MessageSender messageSender;

    private static final String QUEUED = "⌛ Вы находитесь в поиске собеседника, подождите...";
    private static final String IDLE = "\uD83D\uDEA8 У вас сейчас нет собеседника. Начните поиск!";

    public void send(Message message) throws TelegramApiException {
        final long chatId = message.getChatId();
        final int replyToMessageId = message.getMessageId();

        final boolean queued = matchmakingStorage.isQueued(chatId);
        if (queued) {
            messageSender.send(chatId, QUEUED, replyToMessageId, stopMarkup());

            return;
        }

        final boolean chatting = matchmakingStorage.isChatting(chatId);
        if (!chatting) {
            messageSender.send(chatId, IDLE, replyToMessageId, searchMarkup());

            return;
        }

        final long partnerChatId = matchmakingStorage.getPartnerChatId(chatId);
        final String text = message.getText();

        messageSender.send(partnerChatId, text);
    }
}
