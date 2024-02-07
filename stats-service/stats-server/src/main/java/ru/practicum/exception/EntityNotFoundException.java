package ru.practicum.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException() {
    }

    public EntityNotFoundException(String msg) {
        super(msg);
    }
}