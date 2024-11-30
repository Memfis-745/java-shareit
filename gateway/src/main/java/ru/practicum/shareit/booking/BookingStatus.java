package ru.practicum.shareit.booking;

public enum BookingStatus {

    WAITING, //новое бронирование, ожидает одобрения
    APPROVED, // бронирование подтверждено владельцем
    CANCELED, //бронирование отменено создателем
    REJECTED, //бронирование отклонено владельцем
}