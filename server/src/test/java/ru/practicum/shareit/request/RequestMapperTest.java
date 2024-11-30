package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RequestMapperTest {
    User user;
    UserDto userDto1;
    User owner;
    UserDto userDto2;
    Item item;

    @BeforeEach
    public void setUp() {

        user = new User(1L, "user", "user@mail.ru");
        owner = new User(2L, "owner", "owner@mail.ru");
        userDto1 = UserMapper.userToDto(user);
        userDto2 = UserMapper.userToDto(owner);
        item = new Item(1L, "item name", "description item", true, owner, null);

    }

    @Test
    void toItemRequestResponseDtoTest() {
        var original = new ItemRequest();
        original.setId(1L);
        original.setRequestor(user);
        original.setItems(List.of(item));
        original.setDescription("Description");
        original.setCreated(LocalDateTime.now());
        var result = RequestMapper.itemRequestToDto(original);

        assertNotNull(result);
        assertEquals(original.getId(), result.getId());
        assertEquals(original.getDescription(), result.getDescription());
        assertEquals(original.getCreated(), result.getCreated());
        assertNotNull(result.getItems());
    }

    @Test
    void toNewItemRequestTest() {
        var original = ItemRequestDto.builder()
                .description("desc item 1")
                .build();

        var result = RequestMapper.itoToItemRequest(original, user);

        assertNotNull(result);
        assertEquals(original.getDescription(), result.getDescription());
        assertEquals(user, result.getRequestor());
    }

    @Test
    void toItemRequestsResponseDtoTest() {
        var original = new ItemRequest();
        original.setId(1L);
        original.setRequestor(user);
        original.setItems(List.of(item));
        original.setDescription("Description");
        original.setCreated(LocalDateTime.now());
        var itemRequests = new ArrayList<ItemRequest>();
        itemRequests.add(original);
        var result = RequestMapper.itemRequestToDto(original);

        assertNotNull(result);
        assertEquals(original.getId(), result.getId());
        assertEquals(original.getDescription(), result.getDescription());
        assertEquals(original.getCreated(), result.getCreated());
        assertNotNull(result.getItems());
    }
}