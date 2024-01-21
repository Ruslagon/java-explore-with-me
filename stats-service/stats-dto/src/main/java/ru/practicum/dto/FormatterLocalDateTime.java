package ru.practicum.dto;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class FormatterLocalDateTime {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime formToDate(String textDate) {
        return LocalDateTime.parse(textDate, formatter);
    }

    public static String dateToText(LocalDateTime dateTime) {
        return dateTime.format(formatter);
    }
}
