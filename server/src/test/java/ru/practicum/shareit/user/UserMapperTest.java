package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    private User user;
    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "Осип Мандельштам", "osip91@mail.ru");
        userDto = new UserDto(1L, "Осип Мандельштам", "osip91@mail.ru");
    }

    @Test
    void toUserDto() {
        UserDto userDto = UserMapper.userToDto(user);

        assertEquals(1L, userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void toUser() {
        User user = UserMapper.dtoToUser(userDto);

        assertEquals(1L, userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void userDtoToUserWithUser() {
        userDto.setEmail(null);

        User user2 = new User(1L, "Иван Царевич", "car@mail.ru");

        User mappedUser = UserMapper.dtoToUser(userDto, user2);

        assertEquals(1L, mappedUser.getId());
        assertEquals("Осип Мандельштам", mappedUser.getName());
        assertEquals("car@mail.ru", mappedUser.getEmail());
    }
}