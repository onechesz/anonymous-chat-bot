package com.ivanminyaev.anonymous_chat_bot.service.matchmaking;

import com.ivanminyaev.anonymous_chat_bot.dao.entity.ChatEntity;
import com.ivanminyaev.anonymous_chat_bot.dao.entity.UserEntity;
import com.ivanminyaev.anonymous_chat_bot.dao.repository.ChatRepository;
import com.ivanminyaev.anonymous_chat_bot.dao.repository.UserRepository;
import com.ivanminyaev.anonymous_chat_bot.exception.NoSuitablePartnerException;
import com.ivanminyaev.anonymous_chat_bot.exception.PartnerNotFoundException;
import com.ivanminyaev.anonymous_chat_bot.exception.UserChattingException;
import com.ivanminyaev.anonymous_chat_bot.exception.UserQueuedException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Component
@Transactional
@Profile(value = "local")
public class InMemoryMatchmakingStorage implements MatchmakingStorage {
    Queue<Long> queue = new ConcurrentLinkedQueue<>();
    Map<Long, Long> dialogs = new ConcurrentHashMap<>();

    UserRepository userRepository;
    ChatRepository chatRepository;

    @Override
    public boolean isQueued(long chatId) {
        final boolean queued = this.queue.contains(chatId);

        return queued;
    }

    @Override
    public boolean isChatting(long chatId) {
        final boolean chatting = this.dialogs.containsKey(chatId);

        return chatting;
    }

    @Override
    public void search(long chatId) {
        if (isQueued(chatId)) {
            throw new UserQueuedException();
        }

        if (isChatting(chatId)) {
            throw new UserChattingException();
        }

        if (this.queue.isEmpty()) {
            this.queue.add(chatId);

            throw new NoSuitablePartnerException();
        }

        final long partnerChatId = queue.poll();
        this.dialogs.put(partnerChatId, chatId);
        this.dialogs.put(chatId, partnerChatId);

        saveChat(chatId, partnerChatId);
    }

    @Override
    public long getPartnerChatId(long chatId) {
        long dialog = Optional.ofNullable(dialogs.get(chatId)).orElseThrow(PartnerNotFoundException::new);

        return dialog;
    }

    @Override
    public void removeFromQueue(long chatId) {
        if (!isQueued(chatId)) {
            return;
        }

        this.queue.remove(chatId);
    }

    @Override
    public void stopDialog(long chatId) {
        Long partnerChatId = dialogs.remove(chatId);

        if (partnerChatId != null) {
            dialogs.remove(partnerChatId);

            closeChat(chatId, partnerChatId);
        }
    }

    private void saveChat(long chatId, long partnerChatId) {
        final UserEntity user1 = userRepository.findByTelegramId(chatId);
        final UserEntity user2 = userRepository.findByTelegramId(partnerChatId);

        final ChatEntity chat = new ChatEntity();
        chat.setUser1(user1);
        chat.setUser2(user2);
        chat.setCreatedAt(LocalDateTime.now());

        chatRepository.save(chat);
    }

    private void closeChat(long chatId, long partnerChatId) {
        ChatEntity chat = chatRepository.findActive(chatId, partnerChatId);

        chat.setClosedAt(LocalDateTime.now());
        chatRepository.save(chat);
    }
}
