package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingDtoOut bookingToDtoOut(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingDtoOut(
                booking.getBookingId(),
                booking.getStartBooking(),
                booking.getFinishBooking(),
                ItemMapper.itemToDto(booking.getItem()),
                UserMapper.userToDto(booking.getBooker()),
                booking.getBookingStatus()
        );
    }

    public static BookingDtoItem bookingToDtoItem(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingDtoItem(
                booking.getBookingId(),
                booking.getStartBooking(),
                booking.getFinishBooking(),
                booking.getBooker().getId(),
                booking.getBookingStatus()
        );
    }

    public static Booking dtoToBooking(BookingDtoIn bookingDtoIn, User user, Item item) {
        return new Booking(
                bookingDtoIn.getId(),
                bookingDtoIn.getStart(),
                bookingDtoIn.getEnd(),
                item,
                user,
                bookingDtoIn.getBookingStatus()
        );
    }
}