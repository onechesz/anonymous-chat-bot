package com.ivanminyaev.anonymous_chat_bot.telegram;

import com.ivanminyaev.anonymous_chat_bot.config.TelegramProperties;
import com.ivanminyaev.anonymous_chat_bot.handler.router.UpdateRouter;
import com.ivanminyaev.anonymous_chat_bot.keyboard.Command;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Arrays;
import java.util.List;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Component
public class AnonymousChatBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    TelegramProperties telegramProperties;
    UpdateRouter updateRouter;
    TelegramClient telegramClient;

    @Override
    public String getBotToken() {
        final String token = telegramProperties.token();

        return token;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        try {
            updateRouter.consume(update);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @PostConstruct
    public void init() throws TelegramApiException {
        final List<BotCommand> commands = Arrays.stream(Command.values())
                .map(command -> new BotCommand(command.getCommand(), command.getText()))
                .toList();

        final SetMyCommands setMyCommands = SetMyCommands.builder().commands(commands).build();

        telegramClient.execute(setMyCommands);
    }
}
