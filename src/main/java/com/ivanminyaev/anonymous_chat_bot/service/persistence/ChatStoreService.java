package com.ivanminyaev.anonymous_chat_bot.service.persistence;

import com.ivanminyaev.anonymous_chat_bot.dao.entity.ChatEntity;
import com.ivanminyaev.anonymous_chat_bot.dao.entity.UserEntity;
import com.ivanminyaev.anonymous_chat_bot.dao.repository.ChatRepository;
import com.ivanminyaev.anonymous_chat_bot.dao.repository.UserRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Service
@Transactional
public class ChatStoreService {
    UserRepository userRepository;
    ChatRepository chatRepository;

    public void saveChat(long chatId, long partnerChatId) {
        final UserEntity user1 = userRepository.findByTelegramId(chatId);
        final UserEntity user2 = userRepository.findByTelegramId(partnerChatId);

        if (user1 == null || user2 == null) {
            return;
        }

        final ChatEntity chat = new ChatEntity();
        chat.setUser1(user1);
        chat.setUser2(user2);
        chat.setCreatedAt(LocalDateTime.now());

        chatRepository.save(chat);
    }

    public void closeChat(long chatId, long partnerChatId) {
        ChatEntity chat = chatRepository.findActive(chatId, partnerChatId);

        chat.setClosedAt(LocalDateTime.now());
        chatRepository.save(chat);
    }
}
