package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;


@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final RequestService requestService;
    static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto saveItemRequest(@RequestHeader(HEADER) long userId,
                                          @RequestBody ItemRequestDto requestDto) {
        log.info("Реквест конроллер. В метод saveItemRequest передан запрос на вещь от пользователя {}" +
                " description: {}, items {}", userId, requestDto.getDescription(), requestDto.getItems());
        return requestService.createItemRequest(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequests(@RequestHeader(HEADER) long requesterId) {
        log.info("В метод getItemRequests передан userId {}", requesterId);
        return requestService.findUserItemRequests(requesterId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getItemRequestsFromOtherUsers(@RequestHeader(HEADER) long userId,
                                                              @RequestParam(defaultValue = "0") int from,
                                                              @RequestParam(defaultValue = "10") int size) {
        log.info("В метод getItemRequestsFromOtherUsers передан userId {}, индекс первого элемента {}, " +
                "количество элементов на странице {}", userId, from, size);
        return requestService.findRequestsAnotherUsers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getOneItemRequest(@RequestHeader(HEADER) long userId, @PathVariable long requestId) {
        log.info("В метод getOneItemRequest передан запрос на получение вещи userId: {}, requestId: {}", userId, requestId);
        return requestService.findOneItemRequest(userId, requestId);
    }

}