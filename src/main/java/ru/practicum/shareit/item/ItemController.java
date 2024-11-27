package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final String headers = "X-Sharer-User-Id";

    @Validated
    @PostMapping
    public ItemDto addItem(@RequestHeader(headers) long userId,
                           @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на добавление новой вещи addItem: userId: {}, name: {}, description: {}, avaliable {}",
                userId, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(headers) long userId,
                              @RequestBody ItemDto itemDto, @PathVariable long itemId
    ) {
        log.info("Запрос на изменение вещи updateItem: userId {}, itemId {}, name: {}, description: {}, " +
                        "avaliable {}",
                userId, itemId, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
        return itemService.updateItem(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoBooking getItemById(@RequestHeader(headers) long userId, @PathVariable("itemId") long itemId) {
        log.info("Запрашивается информация о вещи getItemById: userId {}, itemId {}", userId, itemId);
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDtoBooking> getUserItems(@RequestHeader(headers) long userId) {
        log.info("Собственник запрашивает информацию о вещах getUserItems: userId {}", userId);
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        log.info("Пользователь запрашивает поиск search вещи text: '{}'", text);
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveComment(@RequestHeader(headers) long userId,
                                  @PathVariable long itemId,
                                  @RequestBody CommentDto comment) {
        log.info("В метод saveComment передан userId {}, itemId {}, отзыв с длиной текста: {}",
                userId, itemId, comment.getText().length());
        return itemService.saveComment(userId, itemId, comment);
    }
}
