package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
//import ru.practicum.shareit.service.StartBeforeEnd;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@StartBeforeEnd
public class BookingRequestDto {

    private Long id = 0L;

    @NotNull(message = "Нужно указать дату заказа вещи")
    @FutureOrPresent(message = "Нельзя указывать прошедшую дату")
    private LocalDateTime start;

    @NotNull(message = "Нужно указать дату возврата вещи")
    @Future(message = "Нельзя указывать прошедшую дату")
    private LocalDateTime end;

    @NotNull(message = "Укажите вещь для аренды")
    private Long itemId;

    private BookingStatus status = BookingStatus.WAITING;

}