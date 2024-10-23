package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item addItem(Item item);

    Item update(long itemId, Item itemNew);

    List<Item> findByText(String text);

    Optional<Item> findItemById(long itemId);

    List<Item> findAllUserItems(long userId);
}
