package com.ivanminyaev.anonymous_chat_bot.service.matchmaking;

import com.ivanminyaev.anonymous_chat_bot.exception.NoSuitablePartnerException;
import com.ivanminyaev.anonymous_chat_bot.exception.UserChattingException;
import com.ivanminyaev.anonymous_chat_bot.exception.UserQueuedException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
@Profile(value = "local")
public class InMemoryMatchmakingStorage implements MatchmakingStorage {
    Queue<Long> queue = new ConcurrentLinkedQueue<>();
    Map<Long, Long> dialogs = new ConcurrentHashMap<>();

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
    }

    @Override
    public long getPartnerChatId(long chatId) {
        long dialog = dialogs.get(chatId);

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
        }
    }
}
