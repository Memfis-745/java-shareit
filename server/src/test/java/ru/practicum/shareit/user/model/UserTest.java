package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UserTest {
    @Test
    void userHashCodeTest() {
        User user1 = new User(1L, "ivan", "ivan@mail.ru");
        User user2 = new User(1L, "ivan", "ivan@mail.ru");

        User user3 = new User(2L, "user3", "user3@mail.ru");

        assertNotEquals(user1, user2);
        assertNotEquals(user1, user3);
    }
}