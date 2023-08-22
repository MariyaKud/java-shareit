package ru.practicum.shareit.exeption;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Long id, Class c) {
        super(String.format("%s с id = %s не найден.", c.getName(), id));
    }
}
