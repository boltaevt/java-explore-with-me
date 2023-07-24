package ru.practicum.ewm.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateTimeMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime toLocalDateTime(String stringDateTime) {
        return LocalDateTime.parse(stringDateTime, FORMATTER);
    }

    public static String toStringDateTime(LocalDateTime dateTime) {
        return dateTime.format(FORMATTER);
    }
}
