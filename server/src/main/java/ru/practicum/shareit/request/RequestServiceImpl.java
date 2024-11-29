package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;


    @Transactional
    @Override
    public ItemRequestDto createItemRequest(long userId, ItemRequestDto requestDto) {
        User user = checkUser(userId);
        ItemRequest requestFromDto = RequestMapper.itoToItemRequest(requestDto, user);
        ItemRequest itemRequest = requestRepository.save(requestFromDto);
        log.info("POST Запрос создан в requestRepository с id : {}", itemRequest.getId());
        List<Item> items = itemRepository.findAllByRequestId(itemRequest.getId());
        itemRequest.setItems(items);
        return RequestMapper.itemRequestToDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> findUserItemRequests(long userId) {
        checkUser(userId);
        List<ItemRequest> itemRequests = requestRepository.findAllByRequestor_IdOrderByCreatedDesc(userId);
        List<Long> itemRequestsIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findAllByRequest_IdInOrderByItemId(itemRequestsIds);
        return requestDtoList(itemRequests, items);
    }

    @Override
    public List<ItemRequestDto> findRequestsAnotherUsers(long userId, int from, int size) {
        checkUser(userId);

        Pageable page = PageRequest.of(from, size);
        List<ItemRequest> requests = requestRepository.findAllByRequestor_IdNot(userId, page).getContent();
        List<Long> requestsIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findAllByRequest_Requestor_IdNotAndRequest_IdInOrderByItemId(userId, requestsIds);
        return requestDtoList(requests, items);
    }

    @Override
    public ItemRequestDto findOneItemRequest(long userId, long requestId) {
        checkUser(userId);
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запроса с ID: " + requestId + " нет в базе"));
        List<ItemDto> itemDtos = itemRepository.findAllByRequest_IdInOrderByItemId(List.of(requestId)).stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());

        return RequestMapper.itemRequestToDto(itemRequest, itemDtos);
    }

    private List<ItemRequestDto> requestDtoList(List<ItemRequest> requests, List<Item> items) {
        List<ItemRequestDto> requestDtos = new ArrayList<>();
        for (ItemRequest request : requests) {
            List<ItemDto> requestItems = new ArrayList<>();
            for (Item item : items) {
                if (item.getRequest().getId() == request.getId()) {
                    requestItems.add(ItemMapper.itemToDto(item));
                }
            }
            requestDtos.add(RequestMapper.itemRequestToDto(request, requestItems));
        }
        return requestDtos;
    }


    public User checkUser(Long userId) {
        log.info("Проверяем юзера с id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("Пользователь с id " +
                                userId + " не зарегистрирован"));
        log.info("Юзер найден: {}", user);
        return user;
    }
}