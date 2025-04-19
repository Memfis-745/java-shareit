package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jdk.jfr.BooleanFlag;
import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Builder
@Entity
@Table(name = "items")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public
class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long itemId;
    @NotBlank(message = "Поле не может быть пустым")
    private String name;
    @NotBlank(message = "Поле не может быть пустым")
    private String description;
    @BooleanFlag
    @NotNull(message = "Поле не может быть null")
    @Column(name = "available")
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;
    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;

}