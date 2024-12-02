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
import java.util.Optional;
import java.util.stream.Collectors;

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
    private ItemService itemService;
    private UserRepository userRepository;

    private ItemDto itemDto;
    private ItemDtoBooking itemDtoBooking;
    private Comment comment;
    private CommentDto commentDto;
    private Booking booking;
    private User user;
    private Item item;
    private ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {
        itemDto = new ItemDto(1L, "Вещь 1", "Описание вещи 1", true, null);
        commentDto = new CommentDto(1L, "Коммент 1", itemDto, "Иван Иванович", LocalDateTime.now());
        user = new User(1L, "Иван Иванович", "ii@mail.ru");
        itemRequest = new ItemRequest(1L, "вещь", user, LocalDateTime.now(), null);
        item = new Item(1L, "Вещь 1", "Описание вещи 1", true, user, null);
        booking = new Booking(1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1), item,
                user, BookingStatus.APPROVED);
        comment = new Comment(1L, "Коммент 1", item, user, LocalDateTime.now());
        itemDtoBooking = new ItemDtoBooking(1L, "ВещьБукинг", "Описание вещи 1", true,
                null, null, null);

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
    void createItemWithWrongRequestId() {
        long requestid = 2L;
        itemDto.setRequestId(requestid);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestRepository.findById(itemDto.getRequestId()))
                .thenThrow(new NotFoundException("Запроса с ID " + itemDto.getRequestId() + " нет в базе"));
        try {
            service.createItem(1L, itemDto);
        } catch (NotFoundException thrown) {
            assertThat(thrown.getMessage(), equalTo("Запроса с ID " + itemDto.getRequestId() + " нет в базе"));
        }

        verify(requestRepository, times(1))
                .findById(anyLong());
        verify(itemRepository, never())
                .save(any());
        verify(userRepository, times(1))
                .findById(anyLong());
    }

    @Test
    void findItemByIdTest() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Item result = itemRepository.findById(1L).orElse(null);
        assertNotNull(result);
        assertEquals(1L, result.getItemId());
        assertEquals("Вещь 1", result.getName());
        assertEquals("Описание вещи 1", result.getDescription());
        assertEquals(user, result.getOwner());
        assertTrue(result.getAvailable());
        assertNull(result.getRequest());
    }

    @Test
    void findItemByWrongIdTest() {
        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.getItemById(1L, 1L));
        assertEquals("Вещь с ID 1 не найдена", ex.getMessage());

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
    void updateItemNotByOwner() {
        User user2 = new User(2L, "Петр Петрович", "pp@mail.ru");
        item.setOwner(user2);
        long userId = 1L;
        long itemId = 1L;

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));

        try {
            service.updateItem(userId, itemDto, itemId);
        } catch (NotFoundException thrown) {
            assertThat(thrown.getMessage(), equalTo("Пользователь с ID " + userId
                    + " не является владельцем вещи c ID " + itemId));
        }
        verify(userRepository, times(1))
                .findById(anyLong());
        verify(itemRepository, times(1))
                .findById(anyLong());
        verify(itemRepository, never())
                .save(any());
        verify(requestRepository, never())
                .findById(anyLong());
    }

    @Test
    public void addCommentSuccessTest() {
        long itemId = 1L;
        long userId = 1L;
        String text = "Test comment";
        CommentDto request = new CommentDto();
        request.setText(text);

        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(new Item()));
        when(bookingRepository.checkValidateBookingsFromItemAndStatus(
                anyLong(), anyLong(), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenAnswer(i -> i.getArguments()[0]);

        CommentDto response = service.saveComment(itemId, userId, request);

        assertNotNull(response);
        assertEquals(request.getText(), response.getText());
        verify(userRepository, times(1))
                .findById(anyLong());
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
    public void mapToCommentDtoListTest() {

        User owner = new User(1L, "owner", "owner@gmail.com");
        Item item = new Item(1L, "item1", "itemDesc1", true, owner, null);
        User author = new User(3L, "author", "author@gmail.com");
        Comment comment1 = new Comment(1L, "text1", item, author, LocalDateTime.now());
        Comment comment2 = new Comment(1L, "text2", item, author, LocalDateTime.now());
        List<Comment> commentList = List.of(comment1, comment2);
        List<CommentDto> commentDto = CommentMapper.commentToDtoList(commentList);

        assertNotNull(commentDto);
        assertEquals(commentDto.get(0).getText(), comment1.getText());
        assertEquals(commentDto.get(1).getText(), comment2.getText());
    }

    @Test
    public void addBookingAndCommentTest() {
        User owner = new User(1L, "test@gmail.com", "Tester");
        Item item = Item.builder().itemId(1L).name("item1").description("itemDesc1").owner(owner).build();
        List<Comment> comments = List.of(
                new Comment(1L, "Comment1", item, owner, LocalDateTime.now()),
                new Comment(2L, "Comment2", item, owner, LocalDateTime.now())
        );
        List<Booking> bookings = List.of(new Booking(1L, LocalDateTime.now().minusDays(2),
                        LocalDateTime.now().minusDays(1), item, owner, BookingStatus.APPROVED),
                new Booking(2L, LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(2), item, owner, BookingStatus.APPROVED));
        LocalDateTime now = LocalDateTime.now();

        ItemDtoBooking result = service.addBookingAndComment(item, 1L, comments, bookings, now);

        assertNotNull(result);
        assertEquals(item.getItemId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertNull(result.getAvailable());
        assertNotNull(result.getLastBooking());
        assertNotNull(result.getNextBooking());
        assertNotNull(result.getComments());
        assertEquals(comments.size(), result.getComments().size());
        assertEquals(comments.get(0).getId(), result.getComments().get(0).getId());
        assertEquals(comments.get(1).getId(), result.getComments().get(1).getId());

    }

    @Test
    public void getBookingAndCommentTest() {
        User owner = new User(1L, "Tester", "test@gmail.com");

        Item item = Item.builder().itemId(1L).name("item1").description("itemDesc1").owner(owner).build();
        List<Comment> comments = List.of(
                new Comment(1L, "Comment1", item, owner, LocalDateTime.now()),
                new Comment(2L, "Comment2", item, owner, LocalDateTime.now())
        );
        List<Booking> bookings = List.of(new Booking(1L, LocalDateTime.now().minusDays(2),
                        LocalDateTime.now().minusDays(1), item, owner, BookingStatus.APPROVED),
                new Booking(2L, LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(2), item, owner, BookingStatus.APPROVED));
        LocalDateTime now = LocalDateTime.now();

        ItemDtoBooking result = service.addBookingAndComment(item, 1L, comments, bookings, now);
        List<ItemDtoBooking> commentResult = service.getCommentAndBooking(List.of(item), 1L);

        assertNotNull(result);
        assertNotNull(commentResult);
        assertEquals(item.getItemId(), commentResult.get(0).getId());
        assertEquals(item.getName(), commentResult.get(0).getName());
        assertEquals(item.getDescription(), commentResult.get(0).getDescription());
        assertNull(result.getAvailable());
        assertNotNull(result.getLastBooking());
        assertNotNull(result.getNextBooking());
        assertNotNull(result.getComments());
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

    @Test
    public void getItemInvalidThrowsExceptionTest() {
        Long itemId = 2L;
        Long userId2 = 2L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.getItemById(itemId, userId2));
        assertEquals("Вещь с ID 2 не найдена", ex.getMessage());
    }

    @Test
    public void getItemsInvalidOwnerIdThrowsExceptionTest() {
        Long ownerId = -1L;
        int from = 0;
        int size = 10;

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.getUserItems(ownerId, from, size));
        assertEquals("Пользователь с id -1 не зарегистрирован", ex.getMessage());
    }

    @Test
    public void getItemsInvalidValueThrowsExceptionTest() {
        Long ownerId = 1L;
        int from = -1;
        int size = 10;

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.getUserItems(ownerId, from, size));
        assertEquals("Пользователь с id 1 не зарегистрирован", ex.getMessage());
    }

    @Test
    public void toItemDtoListTest() {
        List<Item> itemList = new ArrayList<>();
        itemList.add(new Item(1L, "item1", "itemDesc1", true, null, null));
        itemList.add(new Item(2L, "item2", "itemDesc2", false, null, null));

        List<ItemDto> expectedResult = new ArrayList<>();
        expectedResult.add(new ItemDto(1L, "item1", "itemDesc1", true, null));
        expectedResult.add(new ItemDto(2L, "item2", "itemDesc2", false, null));

        List<ItemDto> actualResult = itemList.stream()
                .map(ItemMapper::itemToDto).collect(Collectors.toList());

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void searchItemsByTextWhenTextIsBlankTest() {
        List<ItemDto> itemDtoList = service.search("");

        assertEquals(List.of(), itemDtoList);
    }

    @Test
    public void addItemInvalidParamsTest() {
        User owner = new User(1L, "test@gmail.com", "Tester");
        ItemDto newItem = new ItemDto(null, null, null, null, null);
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                service.createItem(owner.getId(), newItem));
        ItemDto itemWithoutName = new ItemDto(null, null, null, true, null);

        assertThrows(NotFoundException.class, () -> service.createItem(owner.getId(), itemWithoutName));
        assertNotNull(exception);

        ItemDto itemWithoutDescription = new ItemDto(null, "testName", null,
                true, null);
        assertThrows(NotFoundException.class, () -> service.createItem(owner.getId(), itemWithoutDescription));
    }

    @Test
    public void addItemWithoutOwnerIdTest() {
        ItemDto itemDto = new ItemDto(null, "Item1", "new item1", true, null);

        assertThrows(NotFoundException.class, () -> {
            service.createItem(9L, itemDto);
        });
    }

    @Test
    public void addCommentItemNotFoundThrowsExceptionTest() {
        Long authorId = 1L;
        Long itemId = 1L;
        CommentDto commentDto = new CommentDto();
        User user = new User();
        user.setId(authorId);
        when(userRepository.findById(authorId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            service.saveComment(itemId, authorId, commentDto);
        });
        assertEquals("Вещь с id 1 не найдена", ex.getMessage());
    }


}