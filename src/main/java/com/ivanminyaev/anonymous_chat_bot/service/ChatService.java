package com.ivanminyaev.anonymous_chat_bot.service;

import com.ivanminyaev.anonymous_chat_bot.dao.entity.ChatEntity;
import com.ivanminyaev.anonymous_chat_bot.dao.entity.ContentEntity;
import com.ivanminyaev.anonymous_chat_bot.dao.entity.MessageEntity;
import com.ivanminyaev.anonymous_chat_bot.dao.entity.UserEntity;
import com.ivanminyaev.anonymous_chat_bot.dao.entity.enumeration.ContentType;
import com.ivanminyaev.anonymous_chat_bot.dao.repository.ChatRepository;
import com.ivanminyaev.anonymous_chat_bot.dao.repository.ContentRepository;
import com.ivanminyaev.anonymous_chat_bot.dao.repository.MessageRepository;
import com.ivanminyaev.anonymous_chat_bot.service.dto.PendingMediaDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.ivanminyaev.anonymous_chat_bot.keyboard.ReplyKeyboardTemplate.searchMarkup;
import static com.ivanminyaev.anonymous_chat_bot.keyboard.ReplyKeyboardTemplate.stopMarkup;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Service
@Transactional
public class ChatService {
    private static final String QUEUED = "<i>Вы находитесь в поиске собеседника, подождите...</i>";
    private static final String IDLE = "<i>У вас сейчас нет собеседника. Начните поиск!</i>";
    private static final long MEDIA_GROUP_SEND_DELAY = 3;

    MatchmakingStorage matchmakingStorage;
    MessageSender messageSender;
    ChatRepository chatRepository;
    MessageRepository messageRepository;
    ContentRepository contentRepository;

    Map<String, List<PendingMediaDto>> pendingMediaGroups = new ConcurrentHashMap<>();
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void send(Message message, boolean hasContent) throws TelegramApiException {
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

        storeMessage(chatId, messageId, text, message);
        send(partnerChatId, text, hasContent, message);
    }

    private void storeMessage(long chatId, int messageId, String text, Message message) {
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
        content.setTelegramFileId(resolveMedia(message));
        contentRepository.save(content);
    }

    private void send(long partnerChatId, String text, boolean hasContent, Message message) throws TelegramApiException {
        if (!hasContent) {
            messageSender.send(partnerChatId, text);

            return;
        }

        final String media = resolveMedia(message);
        final ContentType type = ContentType.of(message);
        final String caption = message.getCaption();
        final String mediaGroupId = message.getMediaGroupId();

        if (mediaGroupId == null) {
            switch (type) {
                case PHOTO -> messageSender.sendPhoto(partnerChatId, media, caption);
                case VIDEO -> messageSender.sendVideo(partnerChatId, media, caption);
                case ANIMATION -> messageSender.sendAnimation(partnerChatId, media, caption);
                case VOICE -> messageSender.sendVoice(partnerChatId, media, caption);
                case VIDEO_NOTE -> messageSender.sendVideoNote(partnerChatId, media);
                case DOCUMENT -> messageSender.sendDocument(partnerChatId, media, caption);
                case STICKER -> messageSender.sendSticker(partnerChatId, media);
                case AUDIO -> messageSender.sendAudio(partnerChatId, media, caption);
            }

            return;
        }

        final List<PendingMediaDto> pendingMediaGroup;

        if (pendingMediaGroups.containsKey(mediaGroupId)) {
            pendingMediaGroup = pendingMediaGroups.get(mediaGroupId);
        } else {
            pendingMediaGroup = new ArrayList<>();

            pendingMediaGroups.put(mediaGroupId, pendingMediaGroup);
            scheduler.schedule(() -> {
                try {
                    sendMediaGroup(partnerChatId, mediaGroupId);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }, MEDIA_GROUP_SEND_DELAY, TimeUnit.SECONDS);
        }

        pendingMediaGroup.add(new PendingMediaDto(media, type, caption));
    }

    private String resolveMedia(Message message) {
        final String media;
        final ContentType type = ContentType.of(message);

        switch (type) {
            case PHOTO -> media = message.getPhoto().getLast().getFileId();
            case VIDEO -> media = message.getVideo().getFileId();
            case ANIMATION -> media = message.getAnimation().getFileId();
            case VOICE -> media = message.getVoice().getFileId();
            case VIDEO_NOTE -> media = message.getVideoNote().getFileId();
            case DOCUMENT -> media = message.getDocument().getFileId();
            case STICKER -> media = message.getSticker().getFileId();
            case AUDIO -> media = message.getAudio().getFileId();
            case null, default -> media = null;
        }

        return media;
    }

    private void sendMediaGroup(long partnerChatId, String mediaGroupId) throws TelegramApiException {
        messageSender.sendMediaGroup(partnerChatId, pendingMediaGroups.get(mediaGroupId));
        pendingMediaGroups.remove(mediaGroupId);
    }
}
