package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class BookingServiceImplContTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final BookingServiceImpl bookingServiceImpl;
    private UserDto testUser;
    private UserDto secondTestUser;
    private ItemDto itemDtoFromDB;
    private BookingDtoIn bookItemRequestDto;
    private BookingDtoIn secondBookItemRequestDto;
    User user;
    Item item;
    private ItemDto itemAvailableFalseDto;
    Item itemAvailableFalse;

    UserDto owner;
    UserDto booker;
    ItemDto itemDtoToCreate;
    BookingDtoIn bookingToCreate;

    @BeforeEach
    public void setUp() {

        ItemDto itemDto = ItemDto.builder().name("ItemDto").description("DescItemDto").available(true).build();
        UserDto userDto = new UserDto(null, "UserDto", "userDto@mail.ru");
        UserDto secondUserDto = new UserDto(null, "UserDto2", "userDto2@mail.ru");
        testUser = userService.createUser(userDto);
        secondTestUser = userService.createUser(secondUserDto);
        user = User.builder().id(1L).name("User").email("user@mail.ru").build();
        itemDtoFromDB = itemService.createItem(testUser.getId(), itemDto);
        item = Item.builder().itemId(1L).name("Item1").description("DescItem1").available(true).owner(user).build();
        itemAvailableFalseDto = ItemDto.builder().id(2L).name("ItemDto").description("descItemDtoFalse").available(false)
                .requestId(user.getId()).build();
        itemAvailableFalse = Item.builder().itemId(2L).name("Item").description("descItemFalse").available(false)
                .owner(user).build();
        bookItemRequestDto = new BookingDtoIn(null, LocalDateTime.now().plusNanos(1), LocalDateTime.now().plusNanos(2),
                itemDtoFromDB.getId(), WAITING);
        secondBookItemRequestDto = new BookingDtoIn(null, LocalDateTime.now().plusNanos(5), LocalDateTime.now().plusNanos(10),
                itemDtoFromDB.getId(), null);
        owner = new UserDto(null, "testUser", "test@email.com");
        booker = new UserDto(null, "testUser2", "test2@email.com");
        itemDtoToCreate = ItemDto.builder().name("testItem").description("testDescription").available(true).build();
        bookingToCreate = new BookingDtoIn(1L, LocalDateTime.now().plusNanos(1), LocalDateTime.now().plusNanos(2),
                null, null);
    }

    @SneakyThrows
    @Test
    void checkRequestTest() {
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> itemService.createItem(user.getId(), itemAvailableFalseDto));
        assertEquals("Запроса с ID " + user.getId() + " нет в базе", ex.getMessage());
    }

    @SneakyThrows
    @Test
    void createBookingTest() {
        BookingDtoOut addBooking = bookingService.addBooking(secondTestUser.getId(), bookItemRequestDto);

        assertNotNull(addBooking);
        checkBookings(addBooking, bookItemRequestDto, secondTestUser, itemDtoFromDB, WAITING);
    }

    @SneakyThrows
    @Test
    void approveBookingTest() {
        BookingDtoOut bookingDtoFromDB = bookingService.addBooking(secondTestUser.getId(), bookItemRequestDto);
        BookingDtoOut approveBooking = bookingService.approveBooking(bookingDtoFromDB.getId(), testUser.getId(),
                true);

        assertNotNull(approveBooking);
        checkBookings(approveBooking, bookItemRequestDto, secondTestUser, itemDtoFromDB, BookingStatus.APPROVED);
    }

    @Test
    void approveBookingWithApprovedTest() {
        BookingDtoOut bookingDtoFromDB = bookingService.addBooking(secondTestUser.getId(), bookItemRequestDto);
        BookingDtoOut waitingBooking = bookingService.approveBooking(bookingDtoFromDB.getId(), testUser.getId(),
                true);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> bookingService.approveBooking(testUser.getId(), bookingDtoFromDB.getId(),
                        true));
        assertEquals("Данное бронирование уже внесено и имеет статус: APPROVED", ex.getMessage());
    }

    @SneakyThrows
    @Test
    void getBookingByIdTest() {
        BookingDtoOut bookingDtoFromDB = bookingService.addBooking(secondTestUser.getId(), bookItemRequestDto);
        BookingDtoOut getBooking = bookingService.getBookingId(bookingDtoFromDB.getId(), secondTestUser.getId());

        assertNotNull(getBooking);
        checkBookings(getBooking, bookItemRequestDto, secondTestUser, itemDtoFromDB, WAITING);
    }

    @Test
    void getBookingByIdTestException() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingId(999L, 1L));
        assertEquals("Запроса на аренду с ID 999 не зарегистрировано", ex.getMessage());
    }

    @SneakyThrows
    @Test
    void getAllBookingsTest() {
        BookingDtoOut bookingDtoFromDB = bookingService.addBooking(secondTestUser.getId(), bookItemRequestDto);
        BookingDtoOut bookingDtoFromDB2 = bookingService.addBooking(secondTestUser.getId(), secondBookItemRequestDto);
        List<BookingDtoOut> bookingDtos = List.of(bookingDtoFromDB, bookingDtoFromDB2);
        List<BookingDtoOut> bookings = bookingService.getAllBookingByUser(secondTestUser.getId(),
                "ALL", 0, 3);

        assertNotNull(bookings);
        assertEquals(bookings.size(), bookingDtos.size());

    }

    @Test
    void getAllBookingsExceptionTest() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> bookingService.getAllBookingByUser(3L, "ALL",
                        0, 3));
        assertEquals("Пользователь с ID 3 не найден", ex.getMessage());
    }

    @SneakyThrows
    @Test
    void getOwnerBookingsTest() {
        BookingDtoOut bookingDtoFromDB = bookingService.addBooking(secondTestUser.getId(), bookItemRequestDto);
        BookingDtoOut bookingDtoFromDB2 = bookingService.addBooking(secondTestUser.getId(), secondBookItemRequestDto);
        List<BookingDtoOut> bookingDtos = List.of(bookingDtoFromDB, bookingDtoFromDB2);
        List<BookingDtoOut> bookings = bookingService
                .getAllBookingByOwner(testUser.getId(), "ALL", 0, 3);

        assertNotNull(bookings);
        assertEquals(bookings.size(), bookingDtos.size());
    }

    @SneakyThrows
    @Test
    void getOwnerBookingsWithStateRejected() {
        BookingDtoOut bookingDtoFromDB = bookingService.addBooking(secondTestUser.getId(), bookItemRequestDto);
        BookingDtoOut bookingDtoFromDB2 = bookingService.approveBooking(bookingDtoFromDB.getId(),
                testUser.getId(), false);
        List<BookingDtoOut> bookings = bookingService
                .getAllBookingByOwner(testUser.getId(), "REJECTED", 0, 3);

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStatus(), BookingStatus.REJECTED);
    }

    @SneakyThrows
    @Test
    void getAllBookingsWithCurrentState() {
        BookingDtoIn bookingDto = new BookingDtoIn(null, LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(2), itemDtoFromDB.getId(), WAITING);

        List<BookingDtoIn> bookingDtos = List.of(bookingDto);
        BookingDtoOut firstBooking = bookingService.addBooking(secondTestUser.getId(), bookingDto);
        bookingService.approveBooking(testUser.getId(), firstBooking.getId(), true);
        List<BookingDtoOut> currentBookings = bookingService.getAllBookingByUser(secondTestUser.getId(),
                "CURRENT", 0, 3);
        BookingDtoOut currentBooking = currentBookings.get(0);

        assertEquals(currentBookings.size(), bookingDtos.size());
    }

    @SneakyThrows
    @Test
    void getAllBookingsWithFutureState() {
        BookingDtoIn bookingDto = new BookingDtoIn(null, LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2), itemDtoFromDB.getId(), WAITING);
        List<BookingDtoIn> bookingDtos = List.of(bookingDto);
        BookingDtoOut firstBooking = bookingService.addBooking(secondTestUser.getId(), bookingDto);
        List<BookingDtoOut> futureBookings = bookingService.getAllBookingByUser(secondTestUser.getId(),
                "FUTURE", 0, 3);
        BookingDtoOut futureBooking = futureBookings.get(0);

        assertEquals(futureBookings.size(), bookingDtos.size());
        assertEquals(futureBooking.getId(), firstBooking.getId());
    }

    @SneakyThrows
    @Test
    void getAllBookingsWithPastState() {
        BookingDtoIn bookingDto = new BookingDtoIn(null, LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1), itemDtoFromDB.getId(), WAITING);
        List<BookingDtoIn> bookingDtos = List.of(bookingDto);
        BookingDtoOut firstBooking = bookingService.addBooking(secondTestUser.getId(), bookingDto);
        bookingService.approveBooking(testUser.getId(), firstBooking.getId(), true);
        List<BookingDtoOut> pastBookings = bookingService.getAllBookingByUser(secondTestUser.getId(), "PAST",
                0, 3);
        BookingDtoOut pastBooking = pastBookings.get(0);

        assertEquals(pastBookings.size(), bookingDtos.size());
        assertEquals(pastBooking.getId(), firstBooking.getId());
    }


    @SneakyThrows
    @Test
    void getAllOwnerBookingsWithFutureState() {
        BookingDtoIn bookingDto = new BookingDtoIn(null, LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2), itemDtoFromDB.getId(), WAITING);
        List<BookingDtoIn> bookingDtos = List.of(bookingDto);
        BookingDtoOut firstBooking = bookingService.addBooking(secondTestUser.getId(), bookingDto);
        bookingService.approveBooking(testUser.getId(), firstBooking.getId(), true);
        List<BookingDtoOut> futureBookings = bookingService.getAllBookingByOwner(testUser.getId(), "FUTURE",
                0, 3);
        BookingDtoOut futureBooking = futureBookings.get(0);

        assertEquals(futureBookings.size(), bookingDtos.size());
        assertEquals(futureBooking.getId(), firstBooking.getId());
    }

    @SneakyThrows
    @Test
    void getAllOwnerBookingsWithPastState() {
        BookingDtoIn bookingDto = new BookingDtoIn(null, LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1), itemDtoFromDB.getId(), WAITING);
        List<BookingDtoIn> bookingDtos = List.of(bookingDto);
        BookingDtoOut firstBooking = bookingService.addBooking(secondTestUser.getId(), bookingDto);
        bookingService.approveBooking(testUser.getId(), firstBooking.getId(), true);

        List<BookingDtoOut> pastBookings = bookingService.getAllBookingByOwner(testUser.getId(),
                "PAST", 0, 3);
        BookingDtoOut pastBooking = pastBookings.get(0);

        assertEquals(pastBookings.size(), bookingDtos.size());
        assertEquals(pastBooking.getId(), firstBooking.getId());
    }

    @Test
    public void checkDatesNegativeTest() {
        BookingDtoIn bookingDto = new BookingDtoIn(null, LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1), itemDtoFromDB.getId(), WAITING);

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> bookingServiceImpl.validateBooking(bookingDto, item, user));
        assertEquals("Нельзя забронировать свою вещь.", ex.getMessage());
    }

    @Test
    public void bookingForItemTest() {
        Booking booking = Booking.builder()
                .bookingId(1L)
                .booker(User.builder().id(2L).build())
                .item(Item.builder().itemId(1L).name("Hole").build())
                .startBooking(LocalDateTime.now())
                .finishBooking(LocalDateTime.now().plusDays(1))
                .build();

        BookingDtoOut itemBookingInfoDto = BookingMapper.bookingToDtoOut(booking);

        assertEquals(1L, itemBookingInfoDto.getId());
        assertEquals(2L, itemBookingInfoDto.getBooker().getId());
        assertEquals(1L, itemBookingInfoDto.getItem().getId());
        assertEquals("Hole", itemBookingInfoDto.getItem().getName());
        assertEquals(booking.getStartBooking(), itemBookingInfoDto.getStart());
        assertEquals(booking.getFinishBooking(), itemBookingInfoDto.getEnd());
    }

    @Test
    public void bookingForItemNegativeTest() {
        Booking booking = Booking.builder()
                .bookingId(1L)
                .booker(User.builder().id(2L).build())
                .item(Item.builder().itemId(1L).name("Hole").build())
                .startBooking(LocalDateTime.now())
                .finishBooking(LocalDateTime.now().plusDays(1))
                .build();

        BookingDtoOut itemBookingInfoDto = BookingMapper.bookingToDtoOut(booking);

        assertNotEquals(2L, itemBookingInfoDto.getId());
        assertNotEquals(1L, itemBookingInfoDto.getBooker().getId());
        assertNotEquals(2L, itemBookingInfoDto.getItem().getId());
        assertNotEquals("Hol", itemBookingInfoDto.getItem().getName());
        assertNotEquals(booking.getStartBooking().plusDays(1), itemBookingInfoDto.getStart());
        assertNotEquals(booking.getFinishBooking().minusHours(2), itemBookingInfoDto.getEnd());
    }

    private void checkBookings(BookingDtoOut booking, BookingDtoIn secondBooking,
                               UserDto user, ItemDto item, BookingStatus status) {
        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStatus(), equalTo(status));
        assertThat(booking.getStart(), equalTo(secondBooking.getStart()));
        assertThat(booking.getEnd(), equalTo(secondBooking.getEnd()));
        assertThat(booking.getBooker().getId(), equalTo(user.getId()));
        assertThat(booking.getItem().getId(), equalTo(item.getId()));
        assertThat(booking.getItem().getName(), equalTo(item.getName()));
    }

    @Test
    void notAvailableItemTest() {
        UserDto createdBooker = userService.createUser(booker);
        BookingDtoIn bookDto = new BookingDtoIn(null, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2),
                2L, null);
        Exception exception = assertThrows(NotFoundException.class, ()
                -> bookingService.addBooking(createdBooker.getId(), bookDto));
        assertEquals("Вещь с ID 2 не найдена", exception.getMessage());
    }

    @Test
    void addBookingAvailableFalseTest() {
        UserDto createdBooker = userService.createUser(booker);
        BookingDtoIn bookDto = new BookingDtoIn(null, LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2), itemAvailableFalse.getItemId(), WAITING);

        Exception exception = assertThrows(NotFoundException.class, ()
                -> bookingService.addBooking(createdBooker.getId(), bookDto));
        assertEquals("Вещь с ID 2 не найдена", exception.getMessage());
    }

    @Test
    public void bookingDtoTest() {
        Booking booking = Booking.builder().bookingId(1L).booker(User.builder().id(2L).build())
                .startBooking(LocalDateTime.now()).finishBooking(LocalDateTime.now().plusDays(1)).build();

        BookingDtoItem itemBookingInfoDto = BookingMapper.bookingToDtoItem(booking);

        assertEquals(1L, itemBookingInfoDto.getId());
        assertEquals(2L, itemBookingInfoDto.getBookerId());
        assertEquals(booking.getStartBooking(), itemBookingInfoDto.getStart());
        assertEquals(booking.getFinishBooking(), itemBookingInfoDto.getEnd());
    }

    @Test
    public void bookingItemDtoTest() {
        Booking booking = Booking.builder()
                .bookingId(1L)
                .booker(User.builder().id(2L).build())
                .startBooking(LocalDateTime.now())
                .finishBooking(LocalDateTime.now().plusDays(1))
                .build();

        BookingDtoItem itemBookingInfoDto = BookingMapper.bookingToDtoItem(booking);

        assertNotEquals(2L, itemBookingInfoDto.getId());
        assertNotEquals(1L, itemBookingInfoDto.getBookerId());
        assertNotEquals(booking.getStartBooking(), itemBookingInfoDto.getEnd());
        assertNotEquals(booking.getFinishBooking(), itemBookingInfoDto.getStart());
    }

    @Test
    public void approveInvalidOwnerTest() {
        Long ownerId = 3L;
        Long bookingId = 2L;
        boolean approved = true;

        Exception exception = assertThrows(EntityNotFoundException.class, () -> bookingService.approveBooking(ownerId,
                bookingId, approved));

        assertEquals("Запроса на аренду с ID 3 не зарегистрировано", exception.getMessage());
    }

    @Test
    public void approveInvalidBookingTest() {
        Long ownerId = 1L;
        Long bookingId = 4L;
        boolean approved = true;

        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                bookingService.approveBooking(bookingId, ownerId, approved));

        assertEquals("Запроса на аренду с ID 4 не зарегистрировано", exception.getMessage());
    }
}