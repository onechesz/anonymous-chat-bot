package com.ivanminyaev.anonymous_chat_bot.dao.entity.enumeration;

import org.telegram.telegrambots.meta.api.objects.message.Message;

public enum ContentType {
    TEXT, PHOTO, VIDEO, ANIMATION, VOICE, VIDEO_NOTE, DOCUMENT, STICKER, AUDIO;

    public static ContentType of(Message message) {
        if (message == null) {
            return null;
        }

        if (message.hasPhoto()) {
            return PHOTO;
        }

        if (message.hasVideo()) {
            return VIDEO;
        }

        if (message.hasAnimation()) {
            return ANIMATION;
        }

        if (message.hasVoice()) {
            return VOICE;
        }

        if (message.hasVideoNote()) {
            return VIDEO_NOTE;
        }

        if (message.hasDocument()) {
            return DOCUMENT;
        }

        if (message.hasSticker()) {
            return STICKER;
        }

        if (message.hasAudio()) {
            return AUDIO;
        }

        return TEXT;
    }
}
