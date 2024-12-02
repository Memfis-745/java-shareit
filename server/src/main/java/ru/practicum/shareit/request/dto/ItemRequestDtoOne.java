package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserRequestDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItemRequestDtoOne {

    private long id;

    private String description;

    private UserRequestDto requestor;

    private LocalDateTime created;

    private List<ItemDto> items;

}