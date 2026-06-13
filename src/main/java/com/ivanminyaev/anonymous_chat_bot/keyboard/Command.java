package com.ivanminyaev.anonymous_chat_bot.keyboard;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public enum Command {
    START("/start", "Запустить бота"),
    SEARCH("/search", "Искать собеседника"),
    STOP("/stop", "Остановить поиск");

    String command;
    String text;

    public static Command of(String text) {
        final Command command = Arrays.stream(Command.values())
                .filter(c -> c.getText().equals(text))
                .findFirst()
                .orElse(null);

        return command;
    }
}
