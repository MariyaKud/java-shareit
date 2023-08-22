package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> getById(Long id);

    Optional<User> getByEmailWithAnotherId(String email, Long id);

    List<User> findAll();

    void delete(Long id);
}
