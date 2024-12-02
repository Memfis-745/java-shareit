package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import java.util.List;

public interface BookingService {
    BookingDtoOut addBooking(long userId, BookingDtoIn bookingDto);

    BookingDtoOut approveBooking(long userId, long bookingId, boolean approved);

    BookingDtoOut getBookingId(long userId, long bookingId);

    List<BookingDtoOut> getAllBookingByUser(long userId, String state, int from, int size);

    List<BookingDtoOut> getAllBookingByOwner(long userId, String state, int from, int size);
}