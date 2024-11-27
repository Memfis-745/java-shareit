package ru.practicum.shareit.booking;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BookingMapperTest {

    private Booking booking;
    private BookingDtoIn bookingDtoIn;
    private Item item;
    private ItemDto itemDto;
    private User user;
    private UserDto userDto;
    private ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "Иван Иванович", "ii@mail.ru");
        userDto = new UserDto(1L, "Иван Иванович", "ii@mail.ru");
        itemRequest = new ItemRequest(1L, "Request 1", user, LocalDateTime.now(), null);
        item = new Item(1L, "Вещь 1", "Описание вещи 1", true, user, itemRequest);
        itemDto = new ItemDto(1L, "Вещь 1", "Описание вещи 1", true, itemRequest.getId());
        booking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(5), item,
                user, BookingStatus.APPROVED);
        bookingDtoIn = new BookingDtoIn(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(5),
                item.getItemId(), BookingStatus.APPROVED);
    }

    @Test
    void toBookingDto() {
        BookingDtoOut bookingDto = BookingMapper.bookingToDtoOut(booking);

        assertEquals(booking.getBookingId(), bookingDto.getId());
        assertEquals(booking.getBookingStatus(), bookingDto.getStatus());
        assertNotNull(bookingDto.getStart());
        assertNotNull(bookingDto.getEnd());
        assertEquals(booking.getItem().getItemId(), bookingDto.getItem().getId());
        assertEquals(booking.getItem().getName(), bookingDto.getItem().getName());
        assertEquals(booking.getItem().getDescription(), bookingDto.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), bookingDto.getItem().getAvailable());
        assertEquals(booking.getItem().getRequest().getId(), bookingDto.getItem().getRequestId());
        assertEquals(booking.getBooker().getId(), bookingDto.getBooker().getId());
        assertEquals(booking.getBooker().getName(), bookingDto.getBooker().getName());
        assertEquals(booking.getBooker().getEmail(), bookingDto.getBooker().getEmail());
    }

    @Test
    void toItemBookingDto() {
        BookingDtoItem bookingDto = BookingMapper.bookingToDtoItem(booking);

        assertEquals(booking.getBookingId(), bookingDto.getId());
        assertEquals(booking.getBookingStatus(), bookingDto.getStatus());
        assertNotNull(bookingDto.getStart());
        assertNotNull(bookingDto.getEnd());
        assertEquals(booking.getBooker().getId(), bookingDto.getBookerId());
    }

    @Test
    void toBooking() {
        Booking mapperBooking = BookingMapper.dtoToBooking(bookingDtoIn, user, item);

        assertEquals(booking.getBookingId(), mapperBooking.getBookingId());
        assertEquals(booking.getBookingStatus(), mapperBooking.getBookingStatus());
        assertNotNull(mapperBooking.getStartBooking());
        assertNotNull(mapperBooking.getFinishBooking());
        assertEquals(booking.getItem().getItemId(), mapperBooking.getItem().getItemId());
        assertEquals(booking.getItem().getName(), mapperBooking.getItem().getName());
        assertEquals(booking.getItem().getDescription(), mapperBooking.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), mapperBooking.getItem().getAvailable());
        assertEquals(booking.getItem().getRequest().getId(), mapperBooking.getItem().getRequest().getId());
        assertEquals(booking.getBooker().getId(), mapperBooking.getBooker().getId());
        assertEquals(booking.getBooker().getName(), mapperBooking.getBooker().getName());
        assertEquals(booking.getBooker().getEmail(), mapperBooking.getBooker().getEmail());

    }
}