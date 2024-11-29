package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;

    @Transactional
    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        ItemRequest request = null;
        if (itemDto.getRequestId() != null) {
            request = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запроса с ID " + itemDto.getRequestId()
                            + " нет в базе"));
        }
        Item itemFromDto = ItemMapper.dtoToItem(itemDto, user, request);
        Item item = itemRepository.save(itemFromDto);
        log.info("Вещь создана и уложена в itemRepository с id : {}, name: {}, owner: {}, available: {}, request: {}",
                item.getItemId(), item.getName(), item.getOwner().getId(), item.getAvailable(), item.getRequest());
        return ItemMapper.itemToDto(item);
    }

    @Transactional
    @Override
    public ItemDto updateItem(long userId, ItemDto itemDto, long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c ID" + userId + " не найден"));

        ItemRequest request = null;
        Item itemOld = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));
        if (itemOld.getOwner().getId() != userId) {
            throw new NotFoundException("Пользователь " + user + " не является владельцем вещи c ID "
                    + itemId);
        }
        Item item = ItemMapper.dtoToItem(itemDto, itemOld, request);
        item.setItemId(itemId);
        return ItemMapper.itemToDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public List<ItemDto> search(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        return itemRepository.findByNameOrDescription(text).stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ItemDtoBooking getItemById(long userId, long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));
        log.info("Вещь найдена item: {}", item);
        return getCommentAndBooking(List.of(item), userId).get(0);
    }

    private List<ItemDtoBooking> getCommentAndBooking(List<Item> items, Long userId) {

        Map<Item, List<Comment>> comments = commentRepository.findByItemIn(
                        items, Sort.by(DESC, "created"))
                .stream()
                .collect(groupingBy(Comment::getItem, toList()));
        Map<Item, List<Booking>> bookings = bookingRepository.findByItemInAndBookingStatus(
                        items, BookingStatus.APPROVED, Sort.by(DESC, "startBooking"))
                .stream()
                .collect(groupingBy(Booking::getItem, toList()));
        LocalDateTime now = LocalDateTime.now();
        return items.stream().map(item -> addBookingAndComment(item, userId, comments.getOrDefault(item, List.of()),
                        bookings.getOrDefault(item, List.of()), now))
                .collect(toList());
    }

    private ItemDtoBooking addBookingAndComment(Item item,
                                                Long userId,
                                                List<Comment> comments,
                                                List<Booking> bookings,
                                                LocalDateTime now) {
        if (item.getOwner().getId().longValue() != userId.longValue()) {
            return ItemMapper.itemToDtoBooking(item, null, null,
                    CommentMapper.commentToDtoList(comments));
        }

        Booking lastBooking = bookings.stream()
                .filter(b -> !b.getStartBooking().isAfter(now))
                .findFirst()
                .orElse(null);

        Booking nextBooking = bookings.stream()
                .filter(b -> b.getStartBooking().isAfter(now))
                .reduce((a, b) -> a.getStartBooking().isBefore(b.getStartBooking()) ? a : b)
                .orElse(null);

        BookingDtoItem lastBookingDto = lastBooking != null ?
                BookingMapper.bookingToDtoItem(lastBooking) : null;

        BookingDtoItem nextBookingDto = nextBooking != null ?
                BookingMapper.bookingToDtoItem(nextBooking) : null;

        return ItemMapper.itemToDtoBooking(item, lastBookingDto, nextBookingDto,
                CommentMapper.commentToDtoList(comments));
    }

    @Override
    public List<ItemDtoBooking> getUserItems(long userId, int from, int size) {
        checkUser(userId);
        Pageable pageable = PageRequest.of(from, size, Sort.unsorted());
        return getCommentAndBooking(itemRepository.findAllByOwnerIdOrderByItemId(userId, pageable).getContent(), userId);
    }

    @Transactional
    @Override
    public CommentDto saveComment(long userId, long itemId, CommentDto commentDto) {

        User user = checkUser(userId);
        Item item = checkItem(itemId);
        log.info("Вещь с id {} найдена", itemId);
        Boolean checkValidate = bookingRepository.checkValidateBookingsFromItemAndStatus(itemId, userId,
                BookingStatus.APPROVED, LocalDateTime.now());
        if (!checkValidate) {
            throw new EntityNotFoundException("Пользователь " + userId + " не арендовывал вещь " + itemId +
                    " и не может писать отзыв");
        }
        Comment comment = CommentMapper.dtoToComment(commentDto, item, user);

        return CommentMapper.commentToDto(commentRepository.save(comment));
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

    private Item checkItem(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с id " +
                        itemId + " не найдена"));
    }

}

