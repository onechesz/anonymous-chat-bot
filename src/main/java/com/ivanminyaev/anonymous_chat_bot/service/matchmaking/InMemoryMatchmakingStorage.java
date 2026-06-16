package com.ivanminyaev.anonymous_chat_bot.service.matchmaking;

import com.ivanminyaev.anonymous_chat_bot.exception.NoSuitablePartnerException;
import com.ivanminyaev.anonymous_chat_bot.exception.PartnerNotFoundException;
import com.ivanminyaev.anonymous_chat_bot.exception.UserChattingException;
import com.ivanminyaev.anonymous_chat_bot.exception.UserQueuedException;
import com.ivanminyaev.anonymous_chat_bot.service.persistence.ChatStoreService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Component
@Profile(value = "local")
public class InMemoryMatchmakingStorage implements MatchmakingStorage {
    Queue<Long> queue = new ConcurrentLinkedQueue<>();
    Map<Long, Long> dialogs = new ConcurrentHashMap<>();

    ChatStoreService chatStoreService;

    @Override
    public boolean isQueued(long chatId) {
        final boolean queued = queue.contains(chatId);

        return queued;
    }

    @Override
    public boolean isChatting(long chatId) {
        final boolean chatting = dialogs.containsKey(chatId);

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

        if (queue.isEmpty()) {
            queue.add(chatId);

            throw new NoSuitablePartnerException();
        }

        final long partnerChatId = queue.poll();

        chatStoreService.saveChat(chatId, partnerChatId);
        dialogs.put(partnerChatId, chatId);
        dialogs.put(chatId, partnerChatId);
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

        queue.remove(chatId);
    }

    @Override
    public void stopDialog(long chatId) {
        Long partnerChatId = dialogs.remove(chatId);

        if (partnerChatId != null) {
            dialogs.remove(partnerChatId);

            chatStoreService.closeChat(chatId, partnerChatId);
        }
    }
}
