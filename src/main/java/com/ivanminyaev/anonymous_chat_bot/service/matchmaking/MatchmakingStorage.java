package com.ivanminyaev.anonymous_chat_bot.service.matchmaking;

public interface MatchmakingStorage {
    boolean isQueued(long chatId);

    boolean isChatting(long chatId);

    void search(long chatId);

    long getPartnerChatId(long chatId);
}
