package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerIdOrderByItemId(Long userId);

    @Query("""
            select it
            from Item as it
            where it.available = true and (it.name
            ilike concat('%', :text, '%') or it.description 
            ilike concat('%', :text, '%'))
            """)
    List<Item> findByNameOrDescription(@Param("text") String text);
}


