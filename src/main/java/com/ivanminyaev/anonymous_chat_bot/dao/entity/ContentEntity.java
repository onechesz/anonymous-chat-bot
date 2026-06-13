package com.ivanminyaev.anonymous_chat_bot.dao.entity;

import com.ivanminyaev.anonymous_chat_bot.dao.entity.enumeration.ContentType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "content")
public class ContentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    MessageEntity message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ContentType type;

    @Column
    String text;

    @Column(name = "telegram_file_id")
    String telegramFileId;
}
