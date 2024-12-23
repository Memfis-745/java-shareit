package ru.practicum.shareit.request.dto;


import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ItemRequestDto {

    private long id;

    private String description;

    private LocalDateTime created;

    private List<ItemDto> items;

}