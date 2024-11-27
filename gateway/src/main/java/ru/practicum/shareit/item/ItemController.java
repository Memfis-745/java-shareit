package ru.practicum.shareit.item;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.service.Create;

import java.util.Collections;


/**
 * TODO Sprint add-controllers.
 */
@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;
    private final String headers = "X-Sharer-User-Id";


    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(headers) Long userId,
                                          @RequestBody @Validated({Create.class}) ItemDto itemDto) {
        log.info("Получен запрос на добавление новой вещи addItem: userId: {}, name: {}, description: {}, avaliable {}, request {}",
                userId, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), itemDto.getRequestId());
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(headers) long userId,
                                             @RequestBody ItemDto itemDto,
                                             @PathVariable long itemId) {
        log.info("Запрос на изменение вещи updateItem: userId {}, itemId {}, name: {}, description: {}, " +
                        "avaliable {}",
                userId, itemId, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
        return itemClient.updateItem(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(headers) long userId, @PathVariable("itemId") long itemId) {
        log.info("Запрашивается информация о вещи getItemById: userId {}, itemId {}", userId, itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader(headers) long userId,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                               @Positive @RequestParam(defaultValue = "20") Integer size) {
        log.info("Собственник запрашивает информацию о вещах getUserItems: userId {}", userId);
        return itemClient.getUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(headers) long userId,
                                         @RequestParam String text
    ) {
        log.info("Пользователь запрашивает поиск search вещи text: '{}'", text);
        if (text == null || text.isBlank()) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        return itemClient.search(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveComment(@RequestHeader(headers) long userId,
                                              @PathVariable long itemId,
                                              @RequestBody CommentDto commentDto) {
        log.info("В метод saveComment передан userId {}, itemId {}, отзыв с длиной текста: {}",
                userId, itemId, commentDto.getText().length());
        return itemClient.saveComment(userId, itemId, commentDto);
    }
}
