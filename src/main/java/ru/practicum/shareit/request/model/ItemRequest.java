package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests")
@Getter
@Setter
public class ItemRequest {

    @Id
    private Long id;
    private String description;
    @Column(name = "user_id")
    private Long requestor;
    private LocalDateTime created;
}
