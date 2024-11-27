package ru.practicum.shareit.booking;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    private BookingServiceImpl bookingService;
    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private UserRepository userRepository;

    private Item item;
    private User user;
    private User user2;
    private Booking booking;
    private BookingDtoIn bookingDtoIn;


    @BeforeEach
    void beforeEach() {
        user = new User(1L, "Иван Иванович", "ii@mail.ru");
        user2 = new User(2L, "Петр Петрович", "pp@mail.ru");
        item = new Item(1L, "Вещь 1", "Описание вещи 1", true, user, null);
        booking = new Booking(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1), item,
                user2, BookingStatus.APPROVED);
        bookingDtoIn = new BookingDtoIn(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1),
                item.getItemId(), BookingStatus.APPROVED);

        //  validationService = mock(ValidationService.class);
        itemRepository = mock(ItemRepository.class);
        bookingRepository = mock(BookingRepository.class);
        userRepository = mock(UserRepository.class);

        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void saveBooking() {
        long userId = 2L;

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(any()))
                .thenReturn(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));


        BookingDtoOut bookingDtoOut = bookingService.addBooking(userId, bookingDtoIn);

        assertThat(bookingDtoOut.getId(), equalTo(bookingDtoIn.getId()));
        assertThat(bookingDtoOut.getStatus(), equalTo(bookingDtoIn.getBookingStatus()));
        assertThat(bookingDtoOut.getBooker().getId(), equalTo(userId));
        assertThat(bookingDtoOut.getItem().getId(), equalTo(bookingDtoIn.getItemId()));
        assertThat(bookingDtoOut.getStart(), notNullValue());
        assertThat(bookingDtoOut.getEnd(), notNullValue());

        verify(userRepository, times(1))
                .findById(anyLong());
        verify(itemRepository, times(1))
                .findById(anyLong());
        verify(bookingRepository, times(1))
                .save(any());
    }


    @Test
    void bookingApprove() {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;
        booking.setBookingStatus(BookingStatus.WAITING);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(booking))
                .thenReturn(booking);

        BookingDtoOut bookingDtoOut = bookingService.approveBooking(userId, bookingId, approved);

        assertThat(bookingDtoOut.getId(), equalTo(bookingId));
        assertThat(bookingDtoOut.getStatus(), equalTo(BookingStatus.APPROVED));
        assertThat(bookingDtoOut.getItem().getId(), equalTo(booking.getItem().getItemId()));
        assertThat(bookingDtoOut.getStart(), notNullValue());
        assertThat(bookingDtoOut.getEnd(), notNullValue());

        verify(userRepository, times(1))
                .findById(anyLong());
        verify(bookingRepository, times(1))
                .findById(anyLong());
        verify(bookingRepository, times(1))
                .save(any());
    }

    @Test
    void bookingApproveWithWrongUserId() {
        long userId = 3L;
        long bookingId = 1L;
        boolean approved = true;
        when(userRepository.findById(anyLong()))
                .thenThrow(new EntityNotFoundException("Пользователь с таким ID не зарегистрировано"));
        try {
            bookingService.approveBooking(userId, bookingId, approved);
        } catch (EntityNotFoundException thrown) {
            assertThat(thrown.getMessage(), equalTo("Пользователь с таким ID не зарегистрировано"));
        }

        verify(userRepository, times(1))
                .findById(anyLong());
        verify(bookingRepository, never())
                .findById(anyLong());
        verify(bookingRepository, never())
                .save(any());
    }


    @Test
    void bookingApproveWhenBookingStatusIsNotWaiting() {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;
        booking.setBookingStatus(BookingStatus.REJECTED);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        try {
            bookingService.approveBooking(userId, bookingId, approved);
        } catch (ValidationException thrown) {
            assertThat(thrown.getMessage(), equalTo("Данное бронирование уже внесено и имеет статус: " + booking.getBookingStatus()));
        }

        verify(userRepository, times(1))
                .findById(anyLong());
        verify(bookingRepository, times(1))
                .findById(anyLong());
        verify(bookingRepository, never())
                .save(any());
    }

    @Test
    void findBookingById() {
        long userId = 1L;
        long bookingId = 1L;

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        BookingDtoOut bookingDtoOut = bookingService.getBookingId(bookingId, userId);

        assertThat(bookingDtoOut.getId(), equalTo(bookingId));
        assertThat(bookingDtoOut.getStatus(), equalTo(BookingStatus.APPROVED));
        assertThat(bookingDtoOut.getItem().getId(), equalTo(booking.getItem().getItemId()));
        assertThat(bookingDtoOut.getStart(), notNullValue());
        assertThat(bookingDtoOut.getEnd(), notNullValue());

        verify(bookingRepository, times(1))
                .findById(anyLong());
    }


}