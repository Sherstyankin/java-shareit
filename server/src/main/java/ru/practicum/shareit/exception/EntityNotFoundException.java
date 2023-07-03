package ru.practicum.shareit.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String s, Class<?> cls) {
        super(s + " " + cls);
    }
}