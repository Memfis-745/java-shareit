package ru.practicum.shareit.user;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UserRequestDtoTest {
    @Autowired
    private JacksonTester<UserDto> json;
    @Autowired
    private JacksonTester<UserDto> jsonUserDtoRequest;

    @Test
    void itemDtoRequestTest() throws IOException {
        UserDto userDtoRequest = new UserDto(1L, "Ivan", "ivannepyan@mail.ru");
        JsonContent<UserDto> result = jsonUserDtoRequest.write(userDtoRequest);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo("Ivan");
        assertThat(result).extractingJsonPathStringValue("$.email")
                .isEqualTo("ivannepyan@mail.ru");
    }

    @Test
    void itemDtoResponseTest() throws IOException {
        UserDto userDtoResponse = new UserDto(1L, "Ivan", "ivannepyan@mail.ru");
        JsonContent<UserDto> result = json.write(userDtoResponse);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo("Ivan");
        assertThat(result).extractingJsonPathStringValue("$.email")
                .isEqualTo("ivannepyan@mail.ru");
    }
}