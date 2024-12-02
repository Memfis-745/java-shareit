package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings")
@Getter
@Setter
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long bookingId;
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startBooking;
    @Column(name = "end_date", nullable = false)
    private LocalDateTime finishBooking;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;
    @ManyToOne(fetch = FetchType.LAZY)
    private User booker;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;


}
