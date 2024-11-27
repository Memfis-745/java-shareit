package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.exception.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {


    private ItemRepository itemRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private RequestRepository requestRepository;
    private ItemServiceImpl service;
    private UserRepository userRepository;

    private ItemDto itemDto;
    private Comment comment;
    private CommentDto commentDto;
    private Booking booking;
    private User user;
    private Item item;

    @BeforeEach
    void beforeEach() {
        itemDto = new ItemDto(1L, "Вещь 1", "Описание вещи 1", true, null);
        commentDto = new CommentDto(1L, "Коммент 1", itemDto, "Иван Иванович", LocalDateTime.now());
        user = new User(1L, "Иван Иванович", "ii@mail.ru");
        item = new Item(1L, "Вещь 1", "Описание вещи 1", true, user, null);
        booking = new Booking(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1), item, user, BookingStatus.APPROVED);
        comment = new Comment(1L, "Коммент 1", item, user, LocalDateTime.now());

        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        requestRepository = mock(RequestRepository.class);
        service = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository, requestRepository);

    }

    @Test
    void createItem() {
        long userId = 1L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.save(any()))
                .thenReturn(item);

        ItemDto itemFromMethod = service.createItem(userId, itemDto);

        assertThat(itemFromMethod.getId(), equalTo(item.getItemId()));
        assertThat(itemFromMethod.getName(), equalTo(item.getName()));
        assertThat(itemFromMethod.getDescription(), equalTo(item.getDescription()));
        assertThat(itemFromMethod.getAvailable(), equalTo(item.getAvailable()));
        assertThat(itemFromMethod.getRequestId(), nullValue());

        verify(userRepository, times(1))
                .findById(anyLong());
        verify(requestRepository, never())
                .findById(anyLong());
        verify(itemRepository, times(1))
                .save(any());
    }

    @Test
    void createItemWithWrongUserId() {
        long userId = 1L;
        when(userRepository.findById(anyLong()))
                .thenThrow(new EntityNotFoundException("Пользователь с таким ID не зарегистрировано"));
        try {
            service.createItem(userId, itemDto);
        } catch (EntityNotFoundException thrown) {
            assertThat(thrown.getMessage(), equalTo("Пользователь с таким ID не зарегистрировано"));
        }

        verify(userRepository, times(1))
                .findById(anyLong());
        verify(requestRepository, never())
                .findById(anyLong());
        verify(itemRepository, never())
                .save(any());
    }


    @Test
    void updateItem() {
        long userId = 1L;
        long itemId = 1L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(itemRepository.save(any()))
                .thenReturn(item);

        ItemDto itemFromMethod = service.updateItem(userId, itemDto, itemId);

        assertThat(itemFromMethod.getId(), equalTo(itemId));
        assertThat(itemFromMethod.getName(), equalTo(itemDto.getName()));
        assertThat(itemFromMethod.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(itemFromMethod.getAvailable(), equalTo(item.getAvailable()));
        assertThat(itemFromMethod.getRequestId(), nullValue());

        verify(userRepository, times(1))
                .findById(anyLong());
        verify(itemRepository, times(1))
                .findById(anyLong());
        verify(itemRepository, times(1))
                .save(any());
        verify(requestRepository, never())
                .findById(anyLong());
        verify(itemRepository, times(1))
                .save(any());
    }

    @Test
    public void getAllItemsUserTest() {
        Long ownerId = 1L;
        int from = -1;
        int size = 10;

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.getUserItems(ownerId, from, size));
        assertEquals("Пользователь с id 1 не зарегистрирован", ex.getMessage());
    }

    @Test
    public void addCommentAuthorNullThrowExceptionTest() {
        Long authorId = 5L;
        Long itemId = 3L;
        CommentDto commentDto = new CommentDto();
        commentDto.setText("text");

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.saveComment(authorId, itemId, commentDto));
        assertEquals("Пользователь с id 5 не зарегистрирован", ex.getMessage());
    }

}