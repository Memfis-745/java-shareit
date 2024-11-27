package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exceptions.WrongParameterException;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    static final String header = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader(header) long userId,
                                             @Valid @RequestBody BookingRequestDto bookingDto) {
        log.info("Бронирование - Post запрос на создание: userId - {}, bookingDtoIn.itemId - {}, bookingDtoIn.start - {}, bookingDtoIn.end - {}",
                userId, bookingDto.getId(), bookingDto.getStart(), bookingDto.getEnd());
        return bookingClient.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(header) long userId,
                                                 @PathVariable("bookingId") long bookingId,
                                                 @RequestParam Boolean approved) {
        log.info("Бронирование - Patch запрос на подтверждение: userId {}, bookingId {}, статус подтверждения {}",
                userId, bookingId, approved);
        return bookingClient.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingId(@RequestHeader(header) long userId,
                                               @PathVariable("bookingId") long bookingId) {
        log.info("Бронирование - Get запрос на получение данных о бронировании: userId {}, bookingId {}", userId, bookingId);
        return bookingClient.getBookingId(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingByUser(@RequestHeader(header) long userId,
                                                      @RequestParam(defaultValue = "ALL") String state,
                                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState stateParam = BookingState.from(state)
                .orElseThrow(() -> new WrongParameterException("Unknown state: " + state));
        log.info("Бронирование - Get запрос на получение списка бронирования пользователя. userId {}, статус бронирования {}",
                userId, state);
        return bookingClient.getAllBookingByUser(userId, stateParam, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingByOwner(@RequestHeader(header) long userId,
                                                       @RequestParam(defaultValue = "ALL") String state,
                                                       @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                                       @RequestParam(name = "size", defaultValue = "20") @Positive int size) {
        log.info("<Бронирование - Get запрос на получение бронирований собственника: userId {}, " +
                "статус бронирования для поиска {},индекс первого элемента {}, количество элементов на странице {}", userId, state, from, size);
        BookingState stateParam = BookingState.from(state)
                .orElseThrow(() -> new WrongParameterException("Unknown state: " + state));
        return bookingClient.getAllBookingByOwner(userId, stateParam, from, size);
    }
}
