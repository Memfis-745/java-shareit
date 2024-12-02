package ru.practicum.shareit.booking;

import java.util.Optional;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static Optional<BookingState> getState(String state) {
        for (BookingState st : BookingState.values()) {
            if (st.name().equals(state.toUpperCase())) {
                return Optional.of(st);
            }
        }
        return Optional.empty();
    }
}