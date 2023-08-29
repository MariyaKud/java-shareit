package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    //Optional<User> findByUserIdAndEmail(Long userId, String email);
}
