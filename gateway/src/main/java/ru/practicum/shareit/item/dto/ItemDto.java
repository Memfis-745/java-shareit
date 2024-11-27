package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.service.Create;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ItemDto {

    private long id;

    @NotBlank(groups = {Create.class}, message = "Передан предмет без названия")
    private String name;

    @NotBlank(groups = {Create.class}, message = "Передан предмет без описания")
    private String description;

    @NotNull(groups = {Create.class}, message = "Передан предмет без указания доступности")
    private Boolean available;

    private Long requestId;
}