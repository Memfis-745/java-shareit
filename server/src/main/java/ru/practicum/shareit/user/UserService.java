package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, long userId);

    UserDto getUserById(long userId);

    void deleteUser(long userId);

    List<UserDto> getAllUsers();

}