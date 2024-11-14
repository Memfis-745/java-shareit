package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;


public class ItemMapper {
    public static ItemDto ItemToDto(Item item) {
        return new ItemDto(
                item.getItemId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static Item DtoToItem(ItemDto itemDto, User owner, ItemRequest request) {
        return new Item(
                itemDto.getId(),
                itemDto.getName() != null ? itemDto.getName() : null,
                itemDto.getDescription() != null ? itemDto.getDescription() : null,
                itemDto.getAvailable(),
                owner,
                request
        );
    }

    public static Item DtoToItem(ItemDto itemDto, Item itemRep, ItemRequest request) {
        return new Item(
                itemDto.getId(),
                itemDto.getName() != null ? itemDto.getName() : itemRep.getName(),
                itemDto.getDescription() != null ? itemDto.getDescription() : itemRep.getDescription(),
                itemDto.getAvailable() != null ? itemDto.getAvailable() : itemRep.getAvailable(),
                itemRep.getOwner(),
                request
        );
    }

    public static ItemDtoBooking ItemToDtoBooking(Item item,
                                                  BookingDtoItem lastBooking,
                                                  BookingDtoItem nextBooking,
                                                  List<CommentDto> comments) {
        return new ItemDtoBooking(
                item.getItemId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                nextBooking,
                comments
        );
    }

}


