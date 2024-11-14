package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByItemInAndBookingStatus(
            List<Item> items,
            BookingStatus status,
            Sort created);

    @Query
            ("select b from Booking b " +
                    "where b.item.itemId = ?1")
    List<Booking> findAllBookingsByItem(
            Long itemId);

    @Query
            ("select new java.lang.Boolean(COUNT(b) > 0) from Booking b " +
                    "where (b.item.itemId = ?1 " +
                    "and b.bookingStatus = ?3 " +
                    "and b.finishBooking = ?4 " +
                    "or b.finishBooking < ?4) " +
                    "and b.booker.id = ?2")
    Boolean checkValidateBookingsFromItemAndStatus(
            Long itemId, Long userId, BookingStatus status, LocalDateTime end);

    @Query
            ("select b from Booking b " +
                    "where b.item.itemId = ?1 " +
                    "and ?2 between b.startBooking and b.finishBooking")
    List<Booking> checkValidateBookings(
            Long itemId, LocalDateTime bookingDtoStartBookingIsBeforeOrAfter);

    @Query
            ("select b from Booking b " +
                    "where b.booker.id = ?1 " +
                    "order by b.startBooking DESC")
    List<Booking> findAllBookingsByBooker(
            Long userId);

    @Query
            ("select b from Booking b " +
                    "where b.booker.id = ?1 " +
                    "and ?2 between b.startBooking and b.finishBooking " +
                    "order by b.startBooking DESC")
    List<Booking> findAllCurrentBookingsByBooker(
            Long userId,
            LocalDateTime now);

    @Query
            ("select b from Booking b " +
                    "where b.booker.id = ?1 " +
                    "and ?2 > b.finishBooking " +
                    "and b.bookingStatus = ?3 " +
                    "order by b.startBooking DESC")
    List<Booking> findAllPastBookingsByBooker(
            Long userId,
            LocalDateTime now, BookingStatus status);

    @Query
            ("select b from Booking b " +
                    "where b.booker.id = ?1 " +
                    "and b.startBooking > ?2 " +
                    "order by b.startBooking DESC")
    List<Booking> findAllFutureBookingsByBooker(
            Long userId,
            LocalDateTime now);

    @Query
            ("select b from Booking b " +
                    "where b.booker.id = ?1 " +
                    "and b.bookingStatus = ?2 " +
                    "order by b.startBooking DESC")
    List<Booking> findAllWaitingBookingsByBooker(
            Long userId,
            BookingStatus status);

    @Query
            ("select b from Booking b " +
                    "where b.booker.id = ?1 " +
                    "and b.bookingStatus = ?2 " +
                    "or b.bookingStatus = ?3 " +
                    "order by b.startBooking DESC")
    List<Booking> findAllBookingsByBooker(
            Long userId,
            BookingStatus status, BookingStatus st);


    @Query
            ("select b from Booking b " +
                    "where b.item.owner.id = ?1 " +
                    "order by b.startBooking DESC")
    List<Booking> findAllBookingsByOwner(
            Long userId);


    @Query
            ("select b from Booking b " +
                    "where b.item.owner.id = ?1 " +
                    "and ?2 between b.startBooking and b.finishBooking " +
                    "order by b.startBooking DESC")
    List<Booking> findAllCurrentBookingsByOwner(
            Long userId,
            LocalDateTime now);

    @Query
            ("select b from Booking b " +
                    "where b.item.owner.id = ?1 " +
                    "and ?2 > b.finishBooking " +
                    "and b.bookingStatus = ?3 " +
                    "order by b.startBooking DESC")
    List<Booking> findAllPastBookingsByOwner(
            Long userId,
            LocalDateTime now, BookingStatus status);

    @Query
            ("select b from Booking b " +
                    "where b.item.owner.id = ?1 " +
                    "and b.startBooking > ?2 " +
                    "order by b.startBooking DESC")
    List<Booking> findAllFutureBookingsByOwner(
            Long userId,
            LocalDateTime now);

    @Query
            ("select b from Booking b " +
                    "where b.item.owner.id = ?1 " +
                    "and b.bookingStatus = ?2 " +
                    "order by b.startBooking DESC")
    List<Booking> findAllWaitingBookingsByOwner(
            Long userId,
            BookingStatus status);

    @Query
            ("select b from Booking b " +
                    "where b.item.owner.id = ?1 " +
                    "and b.bookingStatus = ?2 " +
                    "or b.bookingStatus = ?3 " +
                    "order by b.startBooking DESC")
    List<Booking> findAllBookingsByOwner(
            Long userId,
            BookingStatus status, BookingStatus st);
}