package ru.practicum.shareit.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final RequestClient requestClient;
    static final String header = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> saveItemRequest(@RequestHeader(header) long userId,
                                                  @RequestBody RequestDto requestDto) {
        log.info("В метод saveItemRequest передан userId {}, itemRequestDto.description: {}",
                userId, requestDto.getDescription());
        return requestClient.createItemRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequests(@RequestHeader(header) long requesterId) { // список СВОИХ запросов
        log.info("В метод getItemRequests передан userId {}", requesterId);
        return requestClient.getUserItemRequests(requesterId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequestsFromOtherUsers(@RequestHeader(header) long userId,
                                                                @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                                @Positive @RequestParam(defaultValue = "20") int size) {
        log.info("В метод getItemRequestsFromOtherUsers передан userId {}, индекс первого элемента {}, " +
                "количество элементов на странице {}", userId, from, size);
        return requestClient.getItemRequestsFromOtherUsers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getOneItemRequest(@RequestHeader(header) long userId, @PathVariable long requestId) {
        log.info("В метод getOneItemRequest передан userId: {}, requestId: {}", userId, requestId);
        return requestClient.getOneItemRequest(userId, requestId);
    }

}