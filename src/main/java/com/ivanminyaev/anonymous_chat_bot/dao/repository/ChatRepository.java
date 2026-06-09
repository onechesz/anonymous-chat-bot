package com.ivanminyaev.anonymous_chat_bot.dao.repository;

import com.ivanminyaev.anonymous_chat_bot.dao.entity.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatRepository extends JpaRepository<ChatEntity, Long> {
    @Query("""
                SELECT c
                FROM ChatEntity c
                WHERE (c.user1.telegramId = :user1TelegramId AND c.user2.telegramId = :user2TelegramId
                    OR c.user1.telegramId = :user2TelegramId AND c.user2.telegramId = :user1TelegramId)
                  AND c.closedAt IS NULL
            """)
    ChatEntity findActive(Long user1TelegramId, Long user2TelegramId);
}
