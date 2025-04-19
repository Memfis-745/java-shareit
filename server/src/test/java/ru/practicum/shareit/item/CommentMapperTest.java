package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CommentMapperTest {

    User user;
    User owner;
    User owner1;
    User requester3;
    UserDto requesterDto3;
    UserDto bookerDto;
    User booker;
    UserDto userDtoTest;
    User userTest;
    Item item1;
    ItemRequestDto itemDto1;
    ItemRequestDto itemRequestDto1;
    ItemRequest itemRequest1;
    ItemDto itemSearchOfTextDto;
    ItemDto itemForItemRequestResponseDto;
    ItemDtoBooking itemWithBookingDto;
    CommentDto commentDto;
    ItemDto itemDtoRequest1;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        UserDto userDto = new UserDto(1L,
                "name",
                "mail@gmail.com"
        );

        user = new User(1L, "user", "user@mail.ru");

        UserDto userDto2 = new UserDto(2L, "owner 2", "user@mail.ru");
        requesterDto3 = new UserDto(null, "requesterDto", null);
        requester3 = new User(requesterDto3.getId(), requesterDto3.getName(), "user@mail.ru");
        owner = new User(2L, "owner 2", "questerDto@gmail.com");
        userDtoTest = new UserDto(null, "userDtoTest", "toForTest@gmail.com");
        userTest = new User(null, userDtoTest.getName(), userDtoTest.getEmail());
        bookerDto = new UserDto(null, "booker", "booker@gmail.com");
        booker = new User(null, bookerDto.getName(), bookerDto.getEmail());
        itemRequest1 = new ItemRequest(1L, " request 1", requester3, now, null);
        itemDtoRequest1 = new ItemDto(1L, "item 1", "description for item 1", true, null);
        item1 = new Item(1L, "item 1", "description for item 1", true, owner1, itemRequest1);
        itemDto1 = ItemRequestDto.builder().description(item1.getDescription()).build();
        itemRequestDto1 = ItemRequestDto.builder().description(item1.getDescription()).build();
        ItemDto itemDtoRequest = new ItemDto(2L, "booking", " some text 1", true, null);
        Item item = ItemMapper.dtoToItem(itemDtoRequest, owner, null);
        commentDto = CommentDto.builder().text("comment 1").build();
        itemForItemRequestResponseDto = new ItemDto(item1.getItemId(), item1.getName(), item1.getDescription(), true, itemRequest1.getId());
        itemWithBookingDto = new ItemDtoBooking(item1.getItemId(), item1.getName(), null, null, null, null, null);

        itemSearchOfTextDto = ItemMapper.itemToDto(item);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void toCommentDtoResponseTest() {
        var original = new Comment();
        original.setId(1L);
        original.setText("Cool");
        original.setItem(item1);
        original.setAuthor(user);
        original.setCreated(LocalDateTime.now());
        var result = CommentMapper.commentToDto(original);

        assertNotNull(result);
        assertEquals(original.getId(), result.getId());
        assertEquals(original.getText(), result.getText());
        assertEquals(original.getAuthor().getName(), result.getAuthorName());
        assertEquals(original.getCreated(), result.getCreated());
    }

    @Test
    void toNewCommentTest() {
        var original = new CommentDto();
        original.setText("Cool");
        var result = CommentMapper.dtoToComment(original, item1, user);

        assertNotNull(result);
        assertEquals(original.getText(), result.getText());
        assertEquals(user, result.getAuthor());
        assertEquals(item1, result.getItem());
        assertNotNull(result.getCreated());
    }

    @Test
    void toCommentDtoListTest() {
        var original = new Comment();
        original.setId(1L);
        original.setText("Cool");
        original.setItem(item1);
        original.setAuthor(user);
        original.setCreated(LocalDateTime.now());
        var comments = new ArrayList<Comment>();
        comments.add(original);
        var result = CommentMapper.commentToDtoList(comments);

        assertNotNull(result);
        assertEquals(comments.get(0).getText(), result.get(0).getText());
        assertEquals(comments.get(0).getAuthor().getName(), result.get(0).getAuthorName());
        assertEquals(comments.get(0).getCreated(), result.get(0).getCreated());
    }
}