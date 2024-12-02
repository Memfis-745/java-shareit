package ru.practicum.shareit.booking;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDtoIn;

import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    protected TestEntityManager entityManager;

    public static User makeUser(Long id, String name, String email) {
        User user = new User(id, name, email);
        return user;
    }

    public static Item makeItem(Long id, String name, String description, User user, boolean available) {
        Item item = new Item(id, name, description, available, user, null);
        return item;
    }

    public static Booking makeBooking(Long id, LocalDateTime start, LocalDateTime end,
                                      Item item, User user, BookingStatus status) {
        Booking booking = new Booking(id, start, end, item, user, status);
        return booking;
    }

    @Test
    public void noFindBookingsIfRepositoryEmpty() {
        Iterable<Booking> bookings = bookingRepository.findAll();

        assertThat(bookings).isEmpty();
    }

    @Test
    public void storeBookingTest() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        User owner = entityManager.persist(makeUser(null, "user", "user@mail.ru"));
        User booker = entityManager.persist(makeUser(null, "user2", "user2@mail.com"));
        Item item = entityManager.persist(makeItem(null, "item", "desc item",
                owner, true));
        Booking booking = bookingRepository.save(new Booking(null, start, end, item, booker,
                BookingStatus.WAITING));

        assertThat(booking)
                .hasFieldOrPropertyWithValue("startBooking", start)
                .hasFieldOrPropertyWithValue("finishBooking", end)
                .hasFieldOrPropertyWithValue("bookingStatus", BookingStatus.WAITING)
                .hasFieldOrProperty("item")
                .hasFieldOrProperty("booker");
        assertThat(booking.getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "item");
    }

    @Test
    public void findAllBookingsByOwnerIdTest() {
        LocalDateTime now = LocalDateTime.now();
        User owner = entityManager.persist(makeUser(null, "user", "user@mail.ru"));
        User booker = entityManager.persist(makeUser(null, "user2", "user2@mail.com"));
        Item item1 = entityManager.persist(makeItem(null, "item", "desc item",
                owner, true));
        Item item2 = entityManager.persist(makeItem(null, "item2", "desc item2",
                owner, true));
        bookingRepository.save(new Booking(null, now.minusDays(2), now.minusDays(1),
                 item1, booker, BookingStatus.WAITING));
        bookingRepository.save(new Booking(null, now.plusDays(1), now.plusDays(2),
                item2, booker, BookingStatus.REJECTED));

        Pageable pageable = PageRequest.of(0, 20);
        List<Booking> listBookings = bookingRepository.findAllBookingsByOwner(owner.getId(), pageable).getContent();

        assertThat(listBookings)
                .hasSize(2)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "item2");
        assertThat(listBookings.get(1).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "item");
    }

    @Test
    public void currentByOwnerIdTest() {
        LocalDateTime now = LocalDateTime.now();
        User owner = entityManager.persist(makeUser(null, "user", "user@mail.ru"));
        User booker = entityManager.persist(makeUser(null, "user2", "user2@mail.com"));
        Item item1 = entityManager.persist(makeItem(null, "item", "desc item",
                owner, true));
        Item item2 = entityManager.persist(makeItem(null, "item2", "desc item2",
                owner, true));

        bookingRepository.save(new Booking(null, now.minusDays(1), now.plusDays(1),
                 item1, booker, BookingStatus.WAITING));
        bookingRepository.save(new Booking(null, now.plusDays(1), now.plusDays(2),
                 item2, booker, BookingStatus.WAITING));

        Pageable pageable = PageRequest.of(0, 20);
        List<Booking> listBookings = bookingRepository.findAllCurrentBookingsByOwner(owner.getId(),
                LocalDateTime.now(), pageable).getContent();

        assertThat(listBookings)
                .hasSize(1)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "item");
    }

    @Test
    public void findPastByOwnerTest() {
        LocalDateTime now = LocalDateTime.now();

        User owner1 = entityManager.persist(makeUser(null, "user", "user@mail.ru"));
        User owner2 = entityManager.persist(makeUser(null, "user2", "user2@mail.com"));
        Item item1 = entityManager.persist(makeItem(null, "item", "desc item",
                owner1, true));
        Item item2 = entityManager.persist(makeItem(null, "item2", "desc item2",
                owner2, true));
        bookingRepository.save(new Booking(null, now.minusDays(2), now.minusDays(1),
                item1, owner1, BookingStatus.APPROVED));
        bookingRepository.save(new Booking(null, now.minusDays(3), now.minusDays(2),
                item2, owner2, BookingStatus.WAITING));

        Pageable pageable = PageRequest.of(0, 20);
        List<Booking> listBookings = bookingRepository.findAllPastBookingsByOwner(owner1.getId(),
                LocalDateTime.now(), BookingStatus.APPROVED, pageable).getContent();

        assertThat(listBookings)
                .hasSize(1)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "item");
    }

    @Test
    public void findFutureByOwnerIdTest() {
        LocalDateTime now = LocalDateTime.now();

        User owner = entityManager.persist(makeUser(null, "user", "user@mail.ru"));
        User booker = entityManager.persist(makeUser(null, "user2", "user2@mail.com"));
        Item item1 = entityManager.persist(makeItem(null, "item", "desc item",
                owner, true));
        Item item2 = entityManager.persist(makeItem(null, "item2", "desc item2",
                owner, true));
        bookingRepository.save(new Booking(null, now.minusDays(2), now.minusDays(1),
                item1, booker, BookingStatus.APPROVED));
        bookingRepository.save(new Booking(null, now.plusDays(1), now.plusDays(2),
                item2, booker, BookingStatus.WAITING));

        Pageable pageable = PageRequest.of(0, 20);
        List<Booking> listBookings = bookingRepository.findAllFutureBookingsByOwner(owner.getId(),
                LocalDateTime.now(), pageable).getContent();

        assertThat(listBookings)
                .hasSize(1)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "item2");
    }

    @Test
    public void findWaitingByOwnerIdTest() {
        LocalDateTime now = LocalDateTime.now();

        User owner = entityManager.persist(makeUser(null, "user", "user@mail.ru"));
        User booker = entityManager.persist(makeUser(null, "user2", "user2@mail.com"));
        Item item1 = entityManager.persist(makeItem(null, "item", "desc item",
                owner, true));
        Item item2 = entityManager.persist(makeItem(null, "item2", "desc item2",
                owner, true));
        bookingRepository.save(new Booking(null, now.minusDays(2), now.minusDays(1),
                item1, booker, BookingStatus.WAITING));
        bookingRepository.save(new Booking(null, now.plusDays(1), now.plusDays(2),
                item2, booker, BookingStatus.WAITING));

        Pageable pageable = PageRequest.of(0, 20);
        List<Booking> listBookings = bookingRepository.findAllWaitingBookingsByOwner(owner.getId(),
                BookingStatus.WAITING, pageable).getContent();

        assertThat(listBookings)
                .hasSize(2)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "item2");
        assertThat(listBookings.get(1).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "item");
    }

    @Test
    public void findRegectedByOwnerIdTest() {
        LocalDateTime now = LocalDateTime.now();

        User owner = entityManager.persist(makeUser(null, "user", "user@mail.ru"));
        User booker = entityManager.persist(makeUser(null, "user2", "user2@mail.com"));
        Item item1 = entityManager.persist(makeItem(null, "item", "desc item",
                owner, true));
        Item item2 = entityManager.persist(makeItem(null, "item2", "desc item2",
                owner, true));
        bookingRepository.save(new Booking(null, now.minusDays(2), now.minusDays(1),
                item1, booker, BookingStatus.CANCELED));
        bookingRepository.save(new Booking(null, now.plusDays(1), now.plusDays(2),
                item2, booker, BookingStatus.REJECTED));

        Pageable pageable = PageRequest.of(0, 20);
        List<Booking> listBookings = bookingRepository.findAllBookingsByOwner(owner.getId(),
                BookingStatus.REJECTED, BookingStatus.CANCELED, pageable).getContent();

        assertThat(listBookings)
                .hasSize(2)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "item2");
        assertThat(listBookings.get(1).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "item");
    }

    @Test
    public void findAllBookingsByBookerIdTest() {
        LocalDateTime now = LocalDateTime.now();
        User owner = entityManager.persist(makeUser(null, "user", "user@mail.ru"));
        User booker = entityManager.persist(makeUser(null, "user2", "user2@mail.com"));
        Item item1 = entityManager.persist(makeItem(null, "item", "desc item",
                owner, true));
        Item item2 = entityManager.persist(makeItem(null, "item2", "desc item2",
                owner, true));
        bookingRepository.save(new Booking(null, now.minusDays(2), now.minusDays(1),
                item1, booker, BookingStatus.WAITING));
        bookingRepository.save(new Booking(null, now.plusDays(1), now.plusDays(2),
                item2, booker, BookingStatus.APPROVED));

        Pageable pageable = PageRequest.of(0, 20);
        List<Booking> listBookings = bookingRepository.findAllBookingsByBooker(booker.getId(), pageable).getContent();

        assertThat(listBookings)
                .hasSize(2)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "item2");
        assertThat(listBookings.get(1).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "item");
    }

    @Test
    public void currentByBookerIdTest() {
        LocalDateTime now = LocalDateTime.now();

        User owner = entityManager.persist(makeUser(null, "user", "user@mail.ru"));
        User booker = entityManager.persist(makeUser(null, "user2", "user2@mail.com"));
        Item item1 = entityManager.persist(makeItem(null, "item", "desc item",
                owner, true));
        Item item2 = entityManager.persist(makeItem(null, "item2", "desc item2",
                owner, true));
        bookingRepository.save(new Booking(null, now.minusDays(2), now.plusDays(1),
                item1, booker, BookingStatus.WAITING));
        bookingRepository.save(new Booking(null, now.plusDays(1), now.plusDays(2),
                item2, booker, BookingStatus.WAITING));


        Pageable pageable = PageRequest.of(0, 20);
        List<Booking> listBookings = bookingRepository.findAllCurrentBookingsByBooker(booker.getId(),
                LocalDateTime.now(), pageable).getContent();

        assertThat(listBookings)
                .hasSize(1)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "item");
    }

    @Test
    public void findPastByBookerTest() {
        LocalDateTime now = LocalDateTime.now();

        User owner = entityManager.persist(makeUser(null, "user", "user@mail.ru"));
        User booker = entityManager.persist(makeUser(null, "user2", "user2@mail.com"));
        Item item1 = entityManager.persist(makeItem(null, "item", "desc item",
                owner, true));
        Item item2 = entityManager.persist(makeItem(null, "item2", "desc item2",
                owner, true));
        bookingRepository.save(new Booking(null, now.minusDays(2), now.minusDays(1),
                item1, booker, BookingStatus.APPROVED));
        bookingRepository.save(new Booking(null, now.plusDays(1), now.plusDays(2),
                item2, booker, BookingStatus.WAITING));

        Pageable pageable = PageRequest.of(0, 20);
        List<Booking> listBookings = bookingRepository.findAllPastBookingsByBooker(booker.getId(),
                LocalDateTime.now(), BookingStatus.APPROVED, pageable).getContent();

        assertThat(listBookings)
                .hasSize(1)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "item");
    }

    @Test
    public void findFutureByBookerIdTest() {
        LocalDateTime now = LocalDateTime.now();


        User owner = entityManager.persist(makeUser(null, "user", "user@mail.ru"));
        User booker = entityManager.persist(makeUser(null, "user2", "user2@mail.com"));
        Item item1 = entityManager.persist(makeItem(null, "item", "desc item",
                owner, true));
        Item item2 = entityManager.persist(makeItem(null, "item2", "desc item2",
                owner, true));
        bookingRepository.save(new Booking(null, now.minusDays(2), now.minusDays(1),
                item1, booker, BookingStatus.WAITING));
        bookingRepository.save(new Booking(null, now.plusDays(1), now.plusDays(2),
                item2, booker, BookingStatus.WAITING));

        Pageable pageable = PageRequest.of(0, 20);
        List<Booking> listBookings = bookingRepository.findAllFutureBookingsByBooker(booker.getId(),
                LocalDateTime.now(), pageable).getContent();

        assertThat(listBookings)
                .hasSize(1)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "item2");
    }

    @Test
    public void findWaitingByBookerIdTest() {
        LocalDateTime now = LocalDateTime.now();

        User owner = entityManager.persist(makeUser(null, "user", "user@mail.ru"));
        User booker = entityManager.persist(makeUser(null, "user2", "user2@mail.com"));
        Item item1 = entityManager.persist(makeItem(null, "item", "desc item",
                owner, true));
        Item item2 = entityManager.persist(makeItem(null, "item2", "desc item2",
                owner, true));
        bookingRepository.save(new Booking(null, now.minusDays(2), now.minusDays(1),
                item1, booker, BookingStatus.WAITING));
        bookingRepository.save(new Booking(null, now.plusDays(1), now.plusDays(2),
                item2, booker, BookingStatus.WAITING));

        Pageable pageable = PageRequest.of(0, 20);
        List<Booking> listBookings = bookingRepository.findAllWaitingBookingsByBooker(booker.getId(),
                BookingStatus.WAITING, pageable).getContent();

        assertThat(listBookings)
                .hasSize(2)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "item2");
        assertThat(listBookings.get(1).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "item");
    }

    @Test
    public void findRegectedByBookerIdTest() {
        LocalDateTime now = LocalDateTime.now();

        User owner = entityManager.persist(makeUser(null, "user", "user@mail.ru"));
        User booker = entityManager.persist(makeUser(null, "user2", "user2@mail.com"));
        Item item1 = entityManager.persist(makeItem(null, "item", "desc item",
                owner, true));
        Item item2 = entityManager.persist(makeItem(null, "item2", "desc item2",
                owner, true));
        bookingRepository.save(new Booking(null, now.minusDays(2), now.minusDays(1),
                item1, booker, BookingStatus.CANCELED));
        bookingRepository.save(new Booking(null, now.plusDays(1), now.plusDays(2),
                item2, booker, BookingStatus.REJECTED));

        Pageable pageable = PageRequest.of(0, 20);
        List<Booking> listBookings = bookingRepository.findAllBookingsByBooker(booker.getId(),
                BookingStatus.REJECTED, BookingStatus.CANCELED, pageable).getContent();

        assertThat(listBookings)
                .hasSize(2)
                .element(0)
                .hasFieldOrProperty("item");
        assertThat(listBookings.get(0).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "item2");
        assertThat(listBookings.get(1).getItem())
                .isInstanceOf(Item.class)
                .hasFieldOrPropertyWithValue("name", "item");
    }

    @Test
    public void validateBookingTest() {
        LocalDateTime now = LocalDateTime.now();
        User owner = entityManager.persist(makeUser(null, "user", "user@mail.ru"));
        User booker = entityManager.persist(makeUser(null, "user2", "user2@mail.com"));
        Item item1 = entityManager.persist(makeItem(null, "item", "desc item",
                owner, true));
        Item item2 = entityManager.persist(makeItem(null, "item2", "desc item2",
                owner, true));
        bookingRepository.save(new Booking(null, now.minusDays(2), now.plusDays(1),
                item1, booker, BookingStatus.WAITING));
        bookingRepository.save(new Booking(null, now.plusDays(1), now.plusDays(2),
                item2, booker, BookingStatus.WAITING));

        BookingDtoIn bookingDtoRequest = new BookingDtoIn(owner.getId(), now.minusDays(2), now.minusDays(1), null, null);
        try {
            bookingRepository.checkValidateBookings(item1.getItemId(), bookingDtoRequest.getStart());
        } catch (ValidationException ex) {
            assertThatExceptionOfType(ValidationException.class);
        }
    }
}