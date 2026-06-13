package com.ivanminyaev.anonymous_chat_bot.service;

import com.ivanminyaev.anonymous_chat_bot.exception.NoSuitablePartnerException;
import com.ivanminyaev.anonymous_chat_bot.exception.PartnerNotFoundException;
import com.ivanminyaev.anonymous_chat_bot.exception.UserChattingException;
import com.ivanminyaev.anonymous_chat_bot.exception.UserQueuedException;
import com.ivanminyaev.anonymous_chat_bot.service.matchmaking.MatchmakingStorage;
import com.ivanminyaev.anonymous_chat_bot.telegram.MessageSender;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.ivanminyaev.anonymous_chat_bot.keyboard.ReplyKeyboardTemplate.searchMarkup;
import static com.ivanminyaev.anonymous_chat_bot.keyboard.ReplyKeyboardTemplate.stopMarkup;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Service
public class MatchmakingService {
    private static final String FOUND = "<i>Собеседник найден. Начинайте общение!</i>";
    private static final String QUEUED = "<i>Вы уже находитесь в поиске, подождите...</i>";
    private static final String CHATTING = "<i>На данный момент вы находитесь в диалоге. Завершите его, чтобы начать поиск нового собеседника: /stop</i>";
    private static final String SEARCHING = "<i>Ищем собеседника...</i>";
    private static final String CHATTING_STOP = "<i>Вы уже нашли собеседника.</i>";
    private static final String IDLE = "<i>Вы не находитесь в поиске.</i>";
    private static final String UNQUEUED = "<i>Поиск собеседника остановлен.</i>";
    private static final String STOP_NO_DIALOG = "<i>У вас сейчас нет собеседника.</i>";
    private static final String STOP = "<i>Вы завершили диалог.</i>";
    private static final String STOP_PARTNER = "<i>Ваш собеседник завершил диалог.</i>";

    MatchmakingStorage matchmakingStorage;
    MessageSender messageSender;

    public void search(Message message) throws TelegramApiException {
        final long chatId = message.getChatId();
        final int messageId = message.getMessageId();

        final SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(FOUND)
                .replyToMessageId(messageId)
                .parseMode("HTML").build();

        try {
            matchmakingStorage.search(chatId);

            long partnerChatId = matchmakingStorage.getPartnerChatId(chatId);
            SendMessage partnerSendMessage = SendMessage.builder()
                    .chatId(partnerChatId)
                    .text(FOUND)
                    .replyMarkup(new ReplyKeyboardRemove(true))
                    .parseMode("HTML").build();
            messageSender.send(partnerSendMessage);

            sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        } catch (UserQueuedException e) {
            sendMessage.setText(QUEUED);
            sendMessage.setReplyMarkup(stopMarkup());
        } catch (UserChattingException e) {
            sendMessage.setText(CHATTING);
        } catch (NoSuitablePartnerException e) {
            sendMessage.setText(SEARCHING);
            sendMessage.setReplyMarkup(stopMarkup());
        }

        messageSender.send(sendMessage);
    }

    public void stop(Message message) throws TelegramApiException {
        final long chatId = message.getChatId();
        final int replyToMessageId = message.getMessageId();

        final boolean chatting = matchmakingStorage.isChatting(chatId);
        if (chatting) {
            messageSender.send(chatId, CHATTING_STOP, replyToMessageId, new ReplyKeyboardRemove(true));

            return;
        }

        final boolean queued = matchmakingStorage.isQueued(chatId);
        if (!queued) {
            messageSender.send(chatId, IDLE, replyToMessageId, searchMarkup());

            return;
        }

        matchmakingStorage.removeFromQueue(chatId);
        messageSender.send(chatId, UNQUEUED, replyToMessageId, searchMarkup());
    }

    public void stopDialog(Message message) throws TelegramApiException {
        final long chatId = message.getChatId();
        final long partnerChatId;
        final int messageId = message.getMessageId();

        try {
            partnerChatId = matchmakingStorage.getPartnerChatId(chatId);
        } catch (PartnerNotFoundException e) {
            messageSender.send(chatId, STOP_NO_DIALOG, messageId);

            return;
        }

        matchmakingStorage.stopDialog(chatId);

        messageSender.send(chatId, STOP, messageId, searchMarkup());
        messageSender.send(partnerChatId, STOP_PARTNER, searchMarkup());
    }
}
