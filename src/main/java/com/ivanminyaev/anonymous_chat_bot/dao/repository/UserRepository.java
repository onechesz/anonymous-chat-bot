package com.ivanminyaev.anonymous_chat_bot.dao.repository;

import com.ivanminyaev.anonymous_chat_bot.dao.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByTelegramId(long telegramId);
}
