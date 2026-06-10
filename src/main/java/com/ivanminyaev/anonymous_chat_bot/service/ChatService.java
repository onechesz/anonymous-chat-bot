package com.ivanminyaev.anonymous_chat_bot.service;

import com.ivanminyaev.anonymous_chat_bot.dao.entity.ChatEntity;
import com.ivanminyaev.anonymous_chat_bot.dao.entity.ContentEntity;
import com.ivanminyaev.anonymous_chat_bot.dao.entity.MessageEntity;
import com.ivanminyaev.anonymous_chat_bot.dao.entity.UserEntity;
import com.ivanminyaev.anonymous_chat_bot.dao.entity.enumeration.ContentType;
import com.ivanminyaev.anonymous_chat_bot.dao.repository.ChatRepository;
import com.ivanminyaev.anonymous_chat_bot.dao.repository.ContentRepository;
import com.ivanminyaev.anonymous_chat_bot.dao.repository.MessageRepository;
import com.ivanminyaev.anonymous_chat_bot.service.matchmaking.MatchmakingStorage;
import com.ivanminyaev.anonymous_chat_bot.telegram.MessageSender;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;

import static com.ivanminyaev.anonymous_chat_bot.keyboard.ReplyKeyboardTemplate.searchMarkup;
import static com.ivanminyaev.anonymous_chat_bot.keyboard.ReplyKeyboardTemplate.stopMarkup;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Service
@Transactional
public class ChatService {
    MatchmakingStorage matchmakingStorage;
    MessageSender messageSender;
    ChatRepository chatRepository;
    MessageRepository messageRepository;
    ContentRepository contentRepository;

    private static final String QUEUED = "⌛ Вы находитесь в поиске собеседника, подождите...";
    private static final String IDLE = "\uD83D\uDEA8 У вас сейчас нет собеседника. Начните поиск!";

    public void send(Message message) throws TelegramApiException {
        final long chatId = message.getChatId();
        final int messageId = message.getMessageId();

        final boolean queued = matchmakingStorage.isQueued(chatId);
        if (queued) {
            messageSender.send(chatId, QUEUED, messageId, stopMarkup());

            return;
        }

        final boolean chatting = matchmakingStorage.isChatting(chatId);
        if (!chatting) {
            messageSender.send(chatId, IDLE, messageId, searchMarkup());

            return;
        }

        final long partnerChatId = matchmakingStorage.getPartnerChatId(chatId);
        final String text = message.getText();

        storeMessage(chatId, messageId, text);
        messageSender.send(partnerChatId, text);
    }

    private void storeMessage(long chatId, int messageId, String text) {
        final ChatEntity chat = chatRepository.findActive(chatId);
        final UserEntity user = chat.getUser1().getTelegramId() == chatId ? chat.getUser1() : chat.getUser2();

        final MessageEntity message = new MessageEntity();
        message.setTelegramId(messageId);
        message.setChat(chat);
        message.setUser(user);
        message.setSentAt(LocalDateTime.now());
        messageRepository.save(message);

        final ContentEntity content = new ContentEntity();
        content.setMessage(message);
        content.setType(ContentType.TEXT); // TODO: temp
        content.setText(text);
        contentRepository.save(content);
    }
}
