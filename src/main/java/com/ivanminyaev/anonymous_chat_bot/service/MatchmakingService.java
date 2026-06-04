package com.ivanminyaev.anonymous_chat_bot.service;

import com.ivanminyaev.anonymous_chat_bot.exception.NoSuitablePartnerException;
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

import static com.ivanminyaev.anonymous_chat_bot.keyboard.ReplyKeyboardTemplate.stopMarkup;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Service
public class MatchmakingService {
    MatchmakingStorage matchmakingStorage;
    MessageSender messageSender;

    private static final String FOUND = "\uD83D\uDE4C Собеседник найден. Начинайте общение!";
    private static final String QUEUED = "⌛ Вы уже находитесь в поиске, подождите...";
    private static final String CHATTING = "\uD83D\uDEA8 На данный момент вы находитесь в диалоге. Завершите его, чтобы начать поиск нового собеседника: /stop";
    private static final String SEARCHING = "⏳ Ищем собеседника...";

    public void search(Message message) throws TelegramApiException {
        final long chatId = message.getChatId();
        final int messageId = message.getMessageId();

        final SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(FOUND)
                .replyToMessageId(messageId).build();

        try {
            matchmakingStorage.search(chatId);

            long partnerChatId = matchmakingStorage.getPartnerChatId(chatId);
            SendMessage partnerSendMessage = SendMessage.builder()
                    .chatId(partnerChatId)
                    .text(FOUND)
                    .replyMarkup(new ReplyKeyboardRemove(true)).build();
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
}
