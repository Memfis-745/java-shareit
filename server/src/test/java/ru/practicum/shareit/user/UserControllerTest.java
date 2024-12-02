package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private static final String PATH_USERS = "/users";
    User user = new User(1L, "user", "user@mail.ru");
    UserDto userDto = new UserDto(user.getId(), user.getName(), user.getEmail());


    @SneakyThrows
    @Test
    void addUserTest() throws Exception {
        when(userService.createUser(any(UserDto.class)))
                .thenReturn(userDto);

        mockMvc.perform(post(PATH_USERS)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @SneakyThrows
    @Test
    public void getAllUsersTest() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(userDto));

        String result = mockMvc.perform(get(PATH_USERS))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(List.of(userDto)), result);
    }

    @SneakyThrows
    @Test
    public void getUsersByIdTest() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(userDto);

        mockMvc.perform(get("/users/{id}", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @SneakyThrows
    @Test
    void updateUserTest() throws Exception {
        when(userService.updateUser(any(UserDto.class), anyLong()))
                .thenReturn(userDto);

        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDto)));
    }

    @SneakyThrows
    @Test
    void deleteUserTestTest() throws Exception {
        mockMvc.perform(delete(PATH_USERS + "/1"))
                .andExpect(status().isOk());
        verify(userService, times(1))
                .deleteUser(anyLong());
    }

    @AfterEach
    void deleteUser() {
        userService.deleteUser(anyLong());
    }
}