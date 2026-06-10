package com.ivanminyaev.anonymous_chat_bot.dao.repository;

import com.ivanminyaev.anonymous_chat_bot.dao.entity.ContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<ContentEntity, Long> {

}
