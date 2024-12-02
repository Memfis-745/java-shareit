package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService service;
    static final String header = "X-Sharer-User-Id";

    @PostMapping
    public BookingDtoOut addBooking(@RequestHeader(header) long userId,
                                    @Valid @RequestBody BookingDtoIn bookingDtoIn) {
        log.info("Бронирование - Post запрос на создание: userId - {}, bookingDtoIn.itemId - {}, bookingDtoIn.start - {}, bookingDtoIn.end - {}",
                userId, bookingDtoIn.getItemId(), bookingDtoIn.getStart(), bookingDtoIn.getEnd());
        return service.addBooking(userId, bookingDtoIn);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut approveBooking(@RequestHeader(header) long userId,
                                        @PathVariable("bookingId") long bookingId,
                                        @RequestParam Boolean approved) {
        log.info("Бронирование - Patch запрос на подтверждение: userId {}, bookingId {}, статус подтверждения {}",
                userId, bookingId, approved);
        return service.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOut getBookingId(@RequestHeader(header) long userId,
                                      @PathVariable("bookingId") long bookingId) {
        log.info("Бронирование - Get запрос на получение данных о бронировании: userId {}, bookingId {}", userId, bookingId);
        return service.getBookingId(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoOut> getAllBookingByUser(@RequestHeader(header) long userId,
                                                   @RequestParam(defaultValue = "ALL") String state,
                                                   @RequestParam(name = "from", defaultValue = "0") int from,
                                                   @RequestParam(name = "size", defaultValue = "20") int size) {
        log.info("Бронирование - Get запрос на получение списка бронирования пользователя. userId {}, статус бронирования {}",
                userId, state);
        return service.getAllBookingByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> getAllBookingByOwner(@RequestHeader(header) long userId,
                                                    @RequestParam(defaultValue = "ALL") String state,
                                                    @RequestParam(name = "from", defaultValue = "0") int from,
                                                    @RequestParam(name = "size", defaultValue = "20") int size) {
        log.info("<Бронирование - Get запрос на получение бронирований собственника: userId {}, " +
                "статус бронирования для поиска {},", userId, state);
        return service.getAllBookingByOwner(userId, state, from, size);
    }
}
