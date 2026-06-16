package com.ivanminyaev.anonymous_chat_bot.service.persistence;

import com.ivanminyaev.anonymous_chat_bot.dao.entity.ChatEntity;
import com.ivanminyaev.anonymous_chat_bot.dao.entity.ContentEntity;
import com.ivanminyaev.anonymous_chat_bot.dao.entity.MessageEntity;
import com.ivanminyaev.anonymous_chat_bot.dao.entity.UserEntity;
import com.ivanminyaev.anonymous_chat_bot.dao.entity.enumeration.ContentType;
import com.ivanminyaev.anonymous_chat_bot.dao.repository.ChatRepository;
import com.ivanminyaev.anonymous_chat_bot.dao.repository.ContentRepository;
import com.ivanminyaev.anonymous_chat_bot.dao.repository.MessageRepository;
import com.ivanminyaev.anonymous_chat_bot.util.MessageHelper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Service
@Transactional
public class MessageStoreService {
    ChatRepository chatRepository;
    MessageRepository messageRepository;
    ContentRepository contentRepository;

    public void store(long chatId, int messageId, String text, Message message) {
        final ChatEntity chat = chatRepository.findActive(chatId);
        final UserEntity user = chat.getUser1().getTelegramId() == chatId ? chat.getUser1() : chat.getUser2();

        final MessageEntity messageEntity = new MessageEntity();
        messageEntity.setTelegramId(messageId);
        messageEntity.setChat(chat);
        messageEntity.setUser(user);
        messageEntity.setSentAt(LocalDateTime.now());
        messageRepository.save(messageEntity);

        final ContentEntity content = new ContentEntity();
        content.setMessage(messageEntity);
        content.setType(ContentType.of(message));
        content.setText(text == null ? message.getCaption() : text);
        content.setTelegramFileId(MessageHelper.resolveMedia(message));
        contentRepository.save(content);
    }
}
