package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jdk.jfr.BooleanFlag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO Sprint add-controllers.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ItemDto {
    private Long id;
    @NotBlank(message = "Поле не может быть пустым")
    private String name;
    @NotBlank(message = "Поле не может быть пустым")
    private String description;
    @BooleanFlag
    @NotNull(message = "Поле не может быть пустым")
    private Boolean available;
    private Long request;

}
