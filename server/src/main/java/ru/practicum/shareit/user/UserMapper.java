package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static UserDto userToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName() != null ? user.getName() : null,
                user.getEmail()
        );
    }

    public static User dtoToUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName() != null ? userDto.getName() : null,
                userDto.getEmail() != null ? userDto.getEmail() : null
        );
    }

    public static User dtoToUser(UserDto userDto, User user) {
        return new User(
                userDto.getId(),
                userDto.getName() != null ? userDto.getName() : user.getName(),
                userDto.getEmail() != null ? userDto.getEmail() : user.getEmail()
        );
    }
}


