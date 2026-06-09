package com.ivanminyaev.anonymous_chat_bot.dao.repository;

import com.ivanminyaev.anonymous_chat_bot.dao.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

}
