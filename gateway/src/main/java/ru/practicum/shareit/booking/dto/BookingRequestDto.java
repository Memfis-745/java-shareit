package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDto {


    private Long id = 0L;

    @NotNull(message = "Нужно указать дату возврата вещи")
    @FutureOrPresent(message = "Нельзя указывать прошедшую дату")
    private LocalDateTime start;

    @NotNull(message = "Нужно указать дату возврата вещи")
    @Future(message = "Нельзя указывать прошедшую дату")
    private LocalDateTime end;

    @NotNull(message = "Нужно указать, какую вещь хотите арендовать")
    private Long itemId;

    private BookingStatus status = BookingStatus.WAITING;

}