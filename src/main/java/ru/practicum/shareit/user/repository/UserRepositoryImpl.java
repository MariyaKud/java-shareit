package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private Long id = 0L;
    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, Long> emails = new HashMap<>();

    @Override
    public Long getId() {
        return id + 1;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User save(User user) {
        final User findUser = users.get(user.getId());

        if (findUser != null) {
            if (!Objects.equals(findUser.getEmail(), user.getEmail())) {
                emails.remove(findUser.getEmail());
            }
        }
        if (id < user.getId()) {
            id = user.getId();
        }

        users.put(user.getId(), user);
        emails.put(user.getEmail(), user.getId());
        return user;
    }

    @Override
    public User getById(Long id) {
        return users.get(id);
    }

    @Override
    public User getByEmail(String email) {
        final Long id = emails.get(email);
        if (id != null) {
            return users.get(id);
        }
        return null;
    }

    @Override
    public User delete(Long id) {
        User user = users.get(id);
        if (user != null) {
            users.remove(id);
            emails.remove(user.getEmail());
        }
        return user;
    }
}
