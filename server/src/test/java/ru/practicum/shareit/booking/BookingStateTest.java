package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingStateTest {

    @Test
    void getState() {
        String stringState = "aLl";
        Optional<BookingState> bookingState = BookingState.getState(stringState);
        assertTrue(bookingState.isPresent());
        assertEquals(BookingState.ALL, bookingState.get());

        stringState = "CurrEnT";
        bookingState = BookingState.getState(stringState);
        assertTrue(bookingState.isPresent());
        assertEquals(BookingState.CURRENT, bookingState.get());

        stringState = "PAST";
        bookingState = BookingState.getState(stringState);
        assertTrue(bookingState.isPresent());
        assertEquals(BookingState.PAST, bookingState.get());

        stringState = "future";
        bookingState = BookingState.getState(stringState);
        assertTrue(bookingState.isPresent());
        assertEquals(BookingState.FUTURE, bookingState.get());

        stringState = "WAITinG";
        bookingState = BookingState.getState(stringState);
        assertTrue(bookingState.isPresent());
        assertEquals(BookingState.WAITING, bookingState.get());

        stringState = "reJecTed";
        bookingState = BookingState.getState(stringState);
        assertTrue(bookingState.isPresent());
        assertEquals(BookingState.REJECTED, bookingState.get());
    }

    @Test
    void values() {
        BookingState[] bookingStates = BookingState.values();

        assertEquals(6, bookingStates.length);
    }

    @Test
    void valueOf() {
        String stringState = "REJECTED";
        BookingState bookingState = BookingState.valueOf(stringState);

        assertEquals(BookingState.REJECTED, bookingState);
    }
}