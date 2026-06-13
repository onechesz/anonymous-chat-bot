package com.ivanminyaev.anonymous_chat_bot.service.dto;

import com.ivanminyaev.anonymous_chat_bot.dao.entity.enumeration.ContentType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PendingMediaDto {
    String media;
    ContentType type;
    String caption;
}
