package ru.practicum.shareit.exception;

public class EntityAlreadyExistException extends RuntimeException {
    public EntityAlreadyExistException(String s, Class<?> cls) {
        super(s + " " + cls);
    }
}
