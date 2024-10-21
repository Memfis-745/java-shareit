package ru.practicum.shareit.user;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Repository
public class UserRepositoryImpl implements UserRepository {

    Map<Long, User> users = new HashMap<>();
    private static long userId = 1;

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User addUser(User user) {
        for (User us : users.values()) {
            if (us.getEmail().equals(user.getEmail())) {
                throw new ValidationException("Пользователь с таким email уже существует");
            }
        }
        user.setId(userId++);
        users.put(user.getId(), user);
        log.info("Пользователь {} сохранен", user);
        return user;
    }

    @Override
    public User updateUser(User userFromDto, long userId) {
        User user = findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        log.info("Пользователь с id = {} - {} найден", user.getId(), user);

        for (User us : users.values()) {
            if (us.getEmail().equals(userFromDto.getEmail())) {
                throw new ValidationException("Пользователь с таким email уже существует");
            }
        }
        if (userFromDto.getName() != null) {
            user.setName(userFromDto.getName());
        }
        if (userFromDto.getEmail() != null) {
            user.setEmail(userFromDto.getEmail());
        }

        log.info("Пользователь {} обновлен", user);
        return users.get(userId);
    }

    @Override
    public Optional<User> findById(Long userId) {

        if (!users.containsKey(userId)) {
            return Optional.empty();
        }
        log.info("Пользователь с id {} найден", userId);
        return Optional.of(users.get(userId));
    }

    @Override
    public void deleteById(Long userId) {
        users.remove(userId);
        log.info("Пользователь с id {} удален", userId);
    }

}

