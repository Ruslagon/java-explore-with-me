package ru.practicum.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String msg) {
        super(msg);
    }
}
