package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        ItemRequest request = null;

        Item itemFromDto = ItemMapper.DtoToItem(itemDto, user, request);
        Item item = itemRepository.addItem(itemFromDto);
        log.info("Отправляем созданный itemDto {}", ItemMapper.ItemToDto(item));
        return ItemMapper.ItemToDto(item);
    }

    @Override
    public ItemDto updateItem(long userId, ItemDto itemDto, long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c ID" + userId + " не найден"));

        ItemRequest request = null;
        Item itemRep = itemRepository.findItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));
        if (itemRep.getOwner().getId() != userId) {
            throw new NotFoundException("Пользователь " + user + " не является владельцем вещи c ID "
                    + itemId);
        }

        Item itemNew = ItemMapper.DtoToItem(itemDto, user, request);
        Item item = itemRepository.update(itemId, itemNew);
        log.info("Отправляем созданный itemDto {}", ItemMapper.ItemToDto(item));
        return ItemMapper.ItemToDto(item);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.findByText(text).stream()
                .map(ItemMapper::ItemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c ID" + userId + " не найден"));
        Item item = itemRepository.findItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));

        return ItemMapper.ItemToDto(item);
    }

    @Override
    public List<ItemDto> getUserItems(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c ID" + userId + " не найден"));

        return itemRepository.findAllUserItems(userId).stream()
                .map(ItemMapper::ItemToDto)
                .collect(Collectors.toList());
    }

}

