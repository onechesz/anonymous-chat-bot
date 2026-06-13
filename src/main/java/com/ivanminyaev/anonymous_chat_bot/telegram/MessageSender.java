package com.ivanminyaev.anonymous_chat_bot.telegram;

import com.ivanminyaev.anonymous_chat_bot.dao.entity.enumeration.ContentType;
import com.ivanminyaev.anonymous_chat_bot.service.dto.PendingMediaDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Component
public class MessageSender {
    TelegramClient telegramClient;

    public void send(SendMessage sendMessage) throws TelegramApiException {
        telegramClient.execute(sendMessage);
    }

    public void send(long chatId, String text) throws TelegramApiException {
        final SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text).build();

        this.send(sendMessage);
    }

    public void send(long chatId, String text, int replyToMessageId) throws TelegramApiException {
        final SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyToMessageId(replyToMessageId)
                .parseMode("HTML").build();

        this.send(sendMessage);
    }

    public void send(long chatId, String text, ReplyKeyboard replyKeyboard) throws TelegramApiException {
        final SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(replyKeyboard)
                .parseMode("HTML").build();

        this.send(sendMessage);
    }

    public void send(long chatId, String text, int replyToMessageId, ReplyKeyboard replyKeyboard) throws TelegramApiException {
        final SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyToMessageId(replyToMessageId)
                .replyMarkup(replyKeyboard)
                .parseMode("HTML").build();

        this.send(sendMessage);
    }

    public void sendPhoto(long chatId, String media, String caption) throws TelegramApiException {
        final InputFile photo = new InputFile(media);

        final SendPhoto sendPhoto = SendPhoto.builder()
                .chatId(chatId)
                .photo(photo)
                .caption(caption).build();

        telegramClient.execute(sendPhoto);
    }

    public void sendVideo(long chatId, String media, String caption) throws TelegramApiException {
        final InputFile video = new InputFile(media);

        final SendVideo sendVideo = SendVideo.builder()
                .chatId(chatId)
                .video(video)
                .caption(caption).build();

        telegramClient.execute(sendVideo);
    }

    public void sendAnimation(long chatId, String media, String caption) throws TelegramApiException {
        final InputFile animation = new InputFile(media);

        final SendAnimation sendAnimation = SendAnimation.builder()
                .chatId(chatId)
                .animation(animation)
                .caption(caption).build();

        telegramClient.execute(sendAnimation);
    }

    public void sendVoice(long chatId, String media, String caption) throws TelegramApiException {
        final InputFile voice = new InputFile(media);

        final SendVoice sendVoice = SendVoice.builder()
                .chatId(chatId)
                .voice(voice)
                .caption(caption).build();

        telegramClient.execute(sendVoice);
    }

    public void sendVideoNote(long chatId, String media) throws TelegramApiException {
        final InputFile videoNote = new InputFile(media);

        final SendVideoNote sendVideoNote = SendVideoNote.builder()
                .chatId(chatId)
                .videoNote(videoNote).build();

        telegramClient.execute(sendVideoNote);
    }

    public void sendDocument(long chatId, String media, String caption) throws TelegramApiException {
        final InputFile document = new InputFile(media);

        final SendDocument sendDocument = SendDocument.builder()
                .chatId(chatId)
                .document(document)
                .caption(caption).build();

        telegramClient.execute(sendDocument);
    }

    public void sendSticker(long chatId, String media) throws TelegramApiException {
        final InputFile sticker = new InputFile(media);

        final SendSticker sendSticker = SendSticker.builder()
                .chatId(chatId)
                .sticker(sticker).build();

        telegramClient.execute(sendSticker);
    }

    public void sendAudio(long chatId, String media, String caption) throws TelegramApiException {
        final InputFile audio = new InputFile(media);

        final SendAudio sendAudio = SendAudio.builder()
                .chatId(chatId)
                .audio(audio)
                .caption(caption).build();

        telegramClient.execute(sendAudio);
    }

    public void sendMediaGroup(long chatId, List<PendingMediaDto> medias) throws TelegramApiException {
        final List<InputMedia> inputMedias = medias.stream().map(media -> {
            final String mediaString = media.getMedia();
            final ContentType contentType = media.getType();

            final InputMedia inputMedia;
            switch (contentType) {
                case PHOTO -> inputMedia = new InputMediaPhoto(mediaString);
                case VIDEO -> inputMedia = new InputMediaVideo(mediaString);
                case DOCUMENT -> inputMedia = new InputMediaDocument(mediaString);
                case AUDIO -> inputMedia = new InputMediaAudio(mediaString);

                default -> inputMedia = new InputMedia() {
                    @Override
                    public String getType() {
                        return "";
                    }
                };
            }

            inputMedia.setCaption(media.getCaption());

            return inputMedia;
        }).toList();

        final SendMediaGroup sendMediaGroup = SendMediaGroup.builder()
                .chatId(chatId)
                .medias(inputMedias).build();

        telegramClient.execute(sendMediaGroup);
    }
}
