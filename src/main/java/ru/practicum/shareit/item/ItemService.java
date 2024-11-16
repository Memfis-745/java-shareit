package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;

import java.util.List;

interface ItemService {
    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, ItemDto itemDto, long itemId);

    ItemDtoBooking getItemById(long userId, long itemId);

    List<ItemDtoBooking> getUserItems(long userId);

    List<ItemDto> search(String text);

    public CommentDto saveComment(long userId, long itemId, CommentDto commentDto);
}
