package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingDtoOut addBooking(long userId, BookingDtoIn bookingDtoIn) {
        User user = checkUser(userId);
        Item item = itemRepository.findById(bookingDtoIn.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + bookingDtoIn.getItemId()
                        + " не найдена"));
        validateBooking(bookingDtoIn, item, user);
        if (!item.getAvailable()) {
            throw new ObjectNotFoundException("У выбранной вещи статус: недоступна");
        }
        if (user.getId().equals(item.getOwner().getId())) {
            throw new NotFoundException("Запрос аренды отправлен владельцем");
        }
        Booking booking = BookingMapper.dtoToBooking(bookingDtoIn, user, item);
        return BookingMapper.bookingToDtoOut(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoOut approveBooking(long bookingId, long userId, boolean approved) {
        checkUser(userId);
        Booking booking = checkBooking(bookingId);
        if (booking.getItem().getOwner().getId() != userId) {
            throw new ValidationException("Пользователь c ID " + userId + " не может менять статус вещи с ID " +
                    booking.getItem().getItemId() + ", так как не является ее владельцем ");
        }
        if (!booking.getBookingStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationException("Данное бронирование уже внесено и имеет статус "
                    + booking.getBookingStatus());
        }
        if (approved) {
            booking.setBookingStatus(BookingStatus.APPROVED);
        } else {
            booking.setBookingStatus(BookingStatus.REJECTED);
        }

        log.info("Присвоили статус: {}", booking.getBookingStatus());
        return BookingMapper.bookingToDtoOut(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDtoOut getBookingId(long bookingId, long userId) {

        Booking booking = checkBooking(bookingId);
        if (booking.getBooker().getId() != userId) {
            if (booking.getItem().getOwner().getId() != userId) {
                throw new NotFoundException("Пользователь " + userId + " не создавал запрос с ID " + bookingId +
                        " и не является владельцем вещи " + booking.getItem().getItemId());
            }
        }
        return BookingMapper.bookingToDtoOut(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDtoOut> getAllBookingByUser(long userId, String state) {
        LocalDateTime now = LocalDateTime.now();
        checkUser(userId);
        log.info("Метод: getAllBookingByUser Проверили юзер и получили его.");
        BookingState bookingState = BookingState.getState(state)
                .orElseThrow(() -> new NotFoundException("Неизвестное состояние: " + state));
        log.info("Метод: getAllBookingByUser Проверили букинг и получили его.");
        List<Booking> bookings = new ArrayList<>();
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllBookingsByBooker(userId);
                log.info("Метод: getAllBookingByUser Получили букинг из свича - ALL.");
                break;
            case CURRENT:
                bookings = bookingRepository.findAllCurrentBookingsByBooker(userId, now);
                break;
            case PAST:
                bookings = bookingRepository.findAllPastBookingsByBooker(userId, now, BookingStatus.APPROVED);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllFutureBookingsByBooker(userId, now);
                break;
            case WAITING:
                bookings = bookingRepository.findAllWaitingBookingsByBooker(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllBookingsByBooker(userId, BookingStatus.REJECTED, BookingStatus.CANCELED);
                break;
        }
        return bookings.stream()
                .map(BookingMapper::bookingToDtoOut)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDtoOut> getAllBookingByOwner(long userId, String state) {
        checkUser(userId);
        LocalDateTime now = LocalDateTime.now();
        BookingState bookingState = BookingState.getState(state)
                .orElseThrow(() -> new NotFoundException("Неизвестное состояние: " + state));
        List<Booking> bookings = new ArrayList<>();
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllBookingsByOwner(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllCurrentBookingsByOwner(userId, now);
                break;
            case PAST:
                bookings = bookingRepository.findAllPastBookingsByOwner(userId, now, BookingStatus.APPROVED);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllFutureBookingsByOwner(userId, now);
                break;
            case WAITING:
                bookings = bookingRepository.findAllWaitingBookingsByOwner(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllBookingsByOwner(userId, BookingStatus.REJECTED, BookingStatus.CANCELED);
                break;
        }
        return bookings.stream()
                .map(BookingMapper::bookingToDtoOut)
                .collect(Collectors.toList());
    }

    public User checkUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь с ID " + userId + " не найден"));
        log.info("Проверили пользователя  и полвозвращаем его - из метода.");
        return user;
    }

    public Booking checkBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("Запроса на аренду с ID " + bookingId
                        + " не зарегистрировано"));
        return booking;
    }

    private void validateBooking(BookingDtoIn bookingDtoRequest, Item item, User booker) {

        List<Booking> bookings = bookingRepository.checkValidateBookings(item.getItemId(), bookingDtoRequest.getStart());
        if (bookings != null && !bookings.isEmpty()) {
            throw new ObjectNotFoundException("Вещь уже забронирована" + item.getName());
        }
    }
}
