package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

@Slf4j
@Component
public class ItemRepositoryImpl implements ItemRepository {
    private Map<Long, Item> items = new HashMap<>();
    private static long id = 1;

    public Item addItem(Item item) {
        if (items.containsValue(item)) {
            throw new NotFoundException("Данный объект существует");
        }

        item.setItemId(id++);
        items.put(item.getItemId(), item);
        log.info("Вещь {} сохранена", item);
        return item;
    }

    @Override
    public Item update(long itemId, Item itemNew) {
        Item item = findItemById(itemId).get();
        if (itemNew.getName() != null) {
            item.setName(itemNew.getName());
        }
        if (itemNew.getDescription() != null) {
            item.setDescription(itemNew.getDescription());
        }
        item.setAvailable(itemNew.getAvailable());

        return item;
    }

    @Override
    public List<Item> findByText(String text) {
        List<Item> itemsSearch = new ArrayList<>();
        for (Item item : items.values()) {
            if ((StringUtils.containsIgnoreCase(item.getName(), text) ||
                    StringUtils.containsIgnoreCase(item.getDescription(), text))
                    && item.getAvailable() == true) {
                itemsSearch.add(item);
            }
        }
        return itemsSearch;
    }

    @Override
    public Optional<Item> findItemById(long itemId) {
        if (!items.containsKey(itemId)) {
            return Optional.empty();
        }
        return Optional.of(items.get(itemId));
    }

    @Override
    public List<Item> findAllUserItems(long userId) {
        return items.values().stream().filter(item ->
                item.getOwner().getId() == userId).collect(Collectors.toList());
    }
}