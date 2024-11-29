package ru.practicum.shareit.item;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ItemMapperTest {

    private User user;
    private ItemRequest itemRequest;
    private Item item;
    private ItemDto itemDto;
    private CommentDto comment;
    private BookingDtoItem bookingLastDto;
    private BookingDtoItem bookingNextDto;


    @BeforeEach
    void beforeEach() {
        user = new User(1L, "Иван Иванович", "ii@mail.ru");
        itemRequest = new ItemRequest(1L, "Request 1", user, LocalDateTime.now(), null);
        item = new Item(1L, "Вещь 1", "Описание вещи 1", true, user, itemRequest);
        bookingLastDto = new BookingDtoItem(1L, LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(5), 1L, BookingStatus.APPROVED);
        bookingNextDto = new BookingDtoItem(2L, LocalDateTime.now().plusHours(12),
                LocalDateTime.now().plusDays(1), 1L, BookingStatus.APPROVED);
        itemDto = new ItemDto(item.getItemId(), item.getName(), item.getDescription(), item.getAvailable(), itemRequest.getId());
        comment = new CommentDto(1L, "Коммент 1", itemDto, user.getName(), LocalDateTime.now());
    }

    @Test
    void toItemDto() {


        ItemDto itemDto = ItemMapper.itemToDto(item);

        assertEquals(item.getItemId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getRequest().getId(), itemDto.getRequestId());
    }

    @Test
    void testToItemDto() {
        ItemDtoBooking itemFromMapper = ItemMapper.itemToDtoBooking(item, bookingLastDto, bookingNextDto, List.of(comment));

        assertEquals(item.getItemId(), itemFromMapper.getId());
        assertEquals(item.getName(), itemFromMapper.getName());
        assertEquals(item.getDescription(), itemFromMapper.getDescription());
        assertEquals(item.getAvailable(), itemFromMapper.getAvailable());
        assertEquals(bookingLastDto.getId(), itemFromMapper.getLastBooking().getId());
        assertEquals(bookingLastDto.getBookerId(), itemFromMapper.getLastBooking().getBookerId());
        assertEquals(bookingLastDto.getStatus(), itemFromMapper.getLastBooking().getStatus());
        assertNotNull(itemFromMapper.getLastBooking().getStart());

        assertEquals(bookingNextDto.getId(), itemFromMapper.getNextBooking().getId());
        assertEquals(bookingNextDto.getBookerId(), itemFromMapper.getNextBooking().getBookerId());
        assertEquals(bookingNextDto.getStatus(), itemFromMapper.getNextBooking().getStatus());
        assertNotNull(itemFromMapper.getNextBooking().getStart());

        assertEquals(1, itemFromMapper.getComments().size());
    }

    @Test
    void toItem() {
        Item mapperItem = ItemMapper.dtoToItem(itemDto, user, itemRequest);

        assertEquals(itemDto.getId(), mapperItem.getItemId());
        assertEquals(itemDto.getName(), mapperItem.getName());
        assertEquals(itemDto.getDescription(), mapperItem.getDescription());
        assertEquals(itemDto.getAvailable(), mapperItem.getAvailable());
        assertEquals(user.getId(), mapperItem.getOwner().getId());
        assertEquals(user.getId(), mapperItem.getOwner().getId());
        assertEquals(user.getEmail(), mapperItem.getOwner().getEmail());
        assertEquals(user.getName(), mapperItem.getOwner().getName());
        assertEquals(itemRequest.getId(), mapperItem.getRequest().getId());
        assertNotNull(itemRequest.getCreated());
        assertEquals(itemRequest.getDescription(), mapperItem.getRequest().getDescription());
    }

    @Test
    void testToItem() {
        Item mapperItem = ItemMapper.dtoToItem(itemDto, item, itemRequest);

        assertEquals(itemDto.getId(), mapperItem.getItemId());
        assertEquals(itemDto.getName(), mapperItem.getName());
        assertEquals(item.getDescription(), mapperItem.getDescription());
        assertEquals(itemDto.getAvailable(), mapperItem.getAvailable());
        assertEquals(itemRequest.getId(), mapperItem.getRequest().getId());
        assertEquals(itemRequest.getDescription(), mapperItem.getRequest().getDescription());
    }
}