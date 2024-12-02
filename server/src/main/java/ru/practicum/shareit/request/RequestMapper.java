package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static ItemRequest itoToItemRequest(ItemRequestDto itemRequestDto, User userRequestor) {

        ItemRequest itemRequest = ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .requestor(userRequestor)
                .created(LocalDateTime.now())
                .build();
        return itemRequest;

    }

    public static ItemRequestDto itemRequestToDto(ItemRequest request) {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(request.getItems().stream()
                        .map(ItemMapper::itemToDto)
                        .collect(Collectors.toList()))
                .build();
        log.info(" реквест id{}", itemRequestDto.getId());
        log.info(" дескриптион id{}", itemRequestDto.getDescription());
        log.info(" дата создания {}", itemRequestDto.getCreated());
        log.info(" items {}", itemRequestDto.getItems());
        return itemRequestDto;

    }

    public static ItemRequestDto itemRequestToDto(ItemRequest request, List<ItemDto> itemsDto) {
        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                itemsDto
        );

    }

}