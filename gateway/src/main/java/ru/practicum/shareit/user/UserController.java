package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.service.Create;
import ru.practicum.shareit.user.dto.UserDto;


/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserClient userClient;

    @Validated
    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody @Validated({Create.class}) UserDto userDto) {
        log.info("Запрос на создание пользователя: name {}, email {}", userDto.getName(), userDto.getEmail());
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto, @PathVariable long userId) {
        log.info("Редактирование пользователя: userId {}, name {}, email {}",
                userId, userDto.getName(), userDto.getEmail());
        return userClient.updateUser(userDto, userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        log.info("Поиск пользователя по id: передан userId {}", userId);
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Поиск всех пользователей");
        return userClient.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("Удаление пользоваетля по userId {}", userId);
        userClient.deleteUser(userId);
    }
}