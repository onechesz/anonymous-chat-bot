package com.ivanminyaev.anonymous_chat_bot.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

public class ReplyKeyboardTemplate {
    public static ReplyKeyboardMarkup searchMarkup() {
        KeyboardRow row = new KeyboardRow(Command.SEARCH.getText());

        ReplyKeyboardMarkup markup = ReplyKeyboardMarkup.builder()
                .keyboardRow(row)
                .resizeKeyboard(true).build();

        return markup;
    }

    public static ReplyKeyboardMarkup stopMarkup() {
        KeyboardRow row = new KeyboardRow(Command.STOP.getText());

        ReplyKeyboardMarkup markup = ReplyKeyboardMarkup.builder()
                .keyboardRow(row)
                .resizeKeyboard(true).build();

        return markup;
    }
}
