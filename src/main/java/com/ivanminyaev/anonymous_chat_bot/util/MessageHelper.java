package com.ivanminyaev.anonymous_chat_bot.util;

import com.ivanminyaev.anonymous_chat_bot.dao.entity.enumeration.ContentType;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public class MessageHelper {
    public static String resolveMedia(Message message) {
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
}
