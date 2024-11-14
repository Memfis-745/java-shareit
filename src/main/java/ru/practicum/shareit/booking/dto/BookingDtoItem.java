package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDtoItem {

    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Long bookerId;

    private BookingStatus status;
}