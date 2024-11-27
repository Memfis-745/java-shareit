package ru.practicum.shareit.request;


import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

interface RequestService {
    ItemRequestDto createItemRequest(long userId, ItemRequestDto requestDto);

    List<ItemRequestDto> findUserItemRequests(long userId);

    List<ItemRequestDto> findRequestsAnotherUsers(long userId, int from, int size);

    ItemRequestDto findOneItemRequest(long userId, long requestId);
}