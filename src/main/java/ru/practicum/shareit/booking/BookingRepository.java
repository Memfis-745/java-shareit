package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByItemInAndBookingStatus(
            List<Item> items,
            BookingStatus status,
            Sort created);

    @Query
            ("""
                    select b from Booking b  
                    where b.item.itemId = :itemId
                    """)
    List<Booking> findAllBookingsByItem(
            @Param("itemId") Long itemId);

    @Query(value = """
            select new java.lang.Boolean(COUNT(b) > 0) 
            from Booking b 
            where (b.item.itemId = :itemId 
            and b.bookingStatus = :status 
            and b.finishBooking = :end 
            or b.finishBooking < :end) 
            and b.booker.id = :userId
            """)
    Boolean checkValidateBookingsFromItemAndStatus(
            @Param("itemId") Long itemId,
            @Param("userId") Long userId,
            @Param("status") BookingStatus status,
            @Param("end") LocalDateTime end);

    @Query
            ("""
                    select b from Booking b 
                    where b.item.itemId = :itemId
                    and :bookingDtoStartBookingIsBeforeOrAfter between b.startBooking and b.finishBooking
                    """)
    List<Booking> checkValidateBookings(
            @Param("itemId") Long itemId,
            @Param("bookingDtoStartBookingIsBeforeOrAfter")
            LocalDateTime bookingDtoStartBookingIsBeforeOrAfter);

    @Query
            ("""
                    select b from Booking b 
                    where b.booker.id = :userId 
                    order by b.startBooking DESC
                    """)
    List<Booking> findAllBookingsByBooker(
            @Param("userId")
            Long userId);

    @Query
            ("""
                    select b from Booking b 
                    where b.booker.id = :userId
                    and :now between b.startBooking and b.finishBooking 
                    order by b.startBooking DESC
                    """)
    List<Booking> findAllCurrentBookingsByBooker(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now);

    @Query
            ("""
                    select b from Booking b 
                    where b.booker.id = :userId
                    and :now > b.finishBooking 
                    and b.bookingStatus = :status
                    order by b.startBooking DESC""")
    List<Booking> findAllPastBookingsByBooker(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now,
            @Param("status") BookingStatus status);


    @Query
            ("""
                    select b from Booking b 
                    where b.booker.id = :userId
                    and b.startBooking > :now
                    order by b.startBooking DESC""")
    List<Booking> findAllFutureBookingsByBooker(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now);

    @Query
            ("""
                    select b from Booking b 
                    where b.booker.id = :userId
                    and b.bookingStatus = :status
                    order by b.startBooking DESC
                    """)
    List<Booking> findAllWaitingBookingsByBooker(
            @Param("userId") Long userId,
            @Param("status") BookingStatus status);

    @Query
            ("""
                    select b from Booking b 
                    where b.booker.id = :userId
                    and b.bookingStatus = :status
                    or b.bookingStatus = :st
                    order by b.startBooking DESC
                    """)
    List<Booking> findAllBookingsByBooker(
            @Param("userId") Long userId,
            @Param("status") BookingStatus status,
            @Param("st") BookingStatus st);

    @Query
            ("""
                    select b from Booking b 
                    where b.item.owner.id = :userId
                    order by b.startBooking DESC
                    """)
    List<Booking> findAllBookingsByOwner(
            @Param("userId") Long userId);

    @Query
            ("""
                    select b from Booking b 
                    where b.item.owner.id = :userId
                    and :now between b.startBooking and b.finishBooking 
                    order by b.startBooking DESC
                    """)
    List<Booking> findAllCurrentBookingsByOwner(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now);

    @Query
            ("""
                    select b from Booking b 
                    where b.item.owner.id = :userId
                    and :now > b.finishBooking 
                    and b.bookingStatus = :status
                    order by b.startBooking DESC
                    """)
    List<Booking> findAllPastBookingsByOwner(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now,
            @Param("status") BookingStatus status);

    @Query
            ("""
                    select b from Booking b 
                    where b.item.owner.id = :userId
                    and b.startBooking > :now
                    order by b.startBooking DESC
                    """)
    List<Booking> findAllFutureBookingsByOwner(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now);

    @Query
            ("""
                    select b from Booking b 
                    where b.item.owner.id = :userId
                    and b.bookingStatus = :status
                    order by b.startBooking DESC
                    """)
    List<Booking> findAllWaitingBookingsByOwner(
            @Param("userId") Long userId,
            @Param("status") BookingStatus status);

    @Query
            ("""
                    select b from Booking b 
                    where b.item.owner.id = :userId
                    and b.bookingStatus = :status
                    or b.bookingStatus = :st
                    order by b.startBooking DESC
                    """)
    List<Booking> findAllBookingsByOwner(
            @Param("userId") Long userId,
            @Param("status") BookingStatus status,
            @Param("st") BookingStatus st);
}