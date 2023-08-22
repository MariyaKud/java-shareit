package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private Long id = 1L;
    private final Map<Long, User> users = new HashMap<>();

    private Long getId() {
        return id++;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(getId());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> getByEmailWithAnotherId(String email, Long id) {
        return users.values()
                    .stream()
                    .filter(f -> !Objects.equals(f.getId(), id) && f.getEmail().equals(email))
                    .findFirst();
    }

    @Override
    public void delete(Long id) {
        User user = users.get(id);
        if (user != null) {
            users.remove(id);
        }
    }
}
