package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDtoItem;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoBooking {

    private long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingDtoItem lastBooking;

    private BookingDtoItem nextBooking;

    private List<CommentDto> comments = new ArrayList<>();
}