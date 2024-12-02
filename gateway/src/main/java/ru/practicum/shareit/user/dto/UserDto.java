package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.service.Create;
import ru.practicum.shareit.service.Update;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;

    private String name;

    @NotBlank(groups = {Create.class}, message = "Передан пустой email")
    @Email(groups = {Create.class, Update.class}, message = "Передан неправильный формат email")
    private String email;
}