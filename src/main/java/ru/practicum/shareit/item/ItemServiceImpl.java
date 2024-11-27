package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
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

    @Transactional
    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        ItemRequest request = null;

        Item itemFromDto = ItemMapper.DtoToItem(itemDto, user, request);
        Item item = itemRepository.save(itemFromDto);
        return ItemMapper.ItemToDto(item);
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
        Item item = ItemMapper.DtoToItem(itemDto, itemOld, request);
        item.setItemId(itemId);
        return ItemMapper.ItemToDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public List<ItemDto> search(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        return itemRepository.findByNameOrDescription(text).stream()
                .map(ItemMapper::ItemToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ItemDtoBooking getItemById(long userId, long itemId) {
        log.info("Пришли в getItemId перед проверкой пользователя");

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));
        log.info("Вещь найдена item: {}", item);
        return GetCommentAndBooking(List.of(item), userId).get(0);
    }

    private List<ItemDtoBooking> GetCommentAndBooking(List<Item> items, Long userId) {

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
            return ItemMapper.ItemToDtoBooking(item, null, null,
                    CommentMapper.CommentToDtoList(comments));
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

        return ItemMapper.ItemToDtoBooking(item, lastBookingDto, nextBookingDto,
                CommentMapper.CommentToDtoList(comments));
    }

    @Override
    public List<ItemDtoBooking> getUserItems(long userId) {
        checkUser(userId);
        return GetCommentAndBooking(itemRepository.findAllByOwnerIdOrderByItemId(userId), userId);
    }

    @Transactional
    @Override
    public CommentDto saveComment(long userId, long itemId, CommentDto commentDto) {

        User user = checkUser(userId);
        Item item = checkItem(itemId);

        Boolean checkValidate = bookingRepository.checkValidateBookingsFromItemAndStatus(itemId, userId,
                BookingStatus.APPROVED, LocalDateTime.now());
        if (!checkValidate) {
            throw new EntityNotFoundException("Пользователь " + userId + " не арендовывал вещь " + itemId +
                    " и не может писать отзыв");
        }
        Comment comment = CommentMapper.DtoToComment(commentDto, item, user);

        return CommentMapper.CommentToDto(commentRepository.save(comment));
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

