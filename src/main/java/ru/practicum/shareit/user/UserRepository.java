package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Collection<User> getAll();

    User addUser(User user);

    User updateUser(User user, long userId);

    Optional<User> findById(Long userId);

    void deleteById(Long userId);
}