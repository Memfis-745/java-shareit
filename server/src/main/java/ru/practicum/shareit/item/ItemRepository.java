package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findAllByOwnerIdOrderByItemId(Long userId, Pageable pageable);

    @Query("""
            select it
            from Item as it
            where it.available = true and (it.name
            ilike concat('%', :text, '%') or it.description
            ilike concat('%', :text, '%'))
            """)
    List<Item> findByNameOrDescription(@Param("text") String text);

    List<Item> findAllByRequest_IdInOrderByItemId(List<Long> itemRequestId);

    @Query("""
            select i from Item i
            where i.request.id = id
            """)
    List<Item> findAllByRequestId(@Param("id") Long id);

    List<Item> findAllByRequest_Requestor_IdNotAndRequest_IdInOrderByItemId(Long userId, List<Long> requestsIds);

}


