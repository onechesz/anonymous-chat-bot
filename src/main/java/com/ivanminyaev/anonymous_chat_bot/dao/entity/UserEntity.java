package com.ivanminyaev.anonymous_chat_bot.dao.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "\"user\"")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "telegram_id", nullable = false, unique = true)
    long telegramId;

    @Column(length = 32)
    String username;

    @Column(name = "first_name", length = 64, nullable = false)
    String firstName;

    @Column(name = "last_name", length = 64)
    String lastName;

    @Column(name = "is_bot", nullable = false)
    boolean isBot;

    @Column(name = "registered_at", nullable = false)
    LocalDateTime registeredAt;

    @Column(name = "last_seen_at", nullable = false)
    LocalDateTime lastSeenAt;
}
