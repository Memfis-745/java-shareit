package ru.practicum.shareit.request;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceImplTest {
    private final RequestService itemRequestService;
    private final UserService userService;
    private final EntityManager em;
    ItemRequest itemRequest1;
    UserDto userDtoRequest;
    UserDto userDtoRequest1;
    User owner1;
    User requester;
    LocalDateTime now;
    LocalDateTime nowPlus10min;
    LocalDateTime nowPlus10hours;
    Item item1;
    ItemRequestDto requestDto;

    TypedQuery<ItemRequest> query;

    @BeforeEach
    void setStart() {
        now = LocalDateTime.now();
        nowPlus10min = now.plusMinutes(10);
        nowPlus10hours = now.plusHours(10);

        userDtoRequest = new UserDto(1L, "name userDto1", "userDto1@mail.ru");
        userDtoRequest1 = new UserDto(2L, "name userDto2", "userDto2@mail.ru");
        owner1 = new User(userDtoRequest.getId(), userDtoRequest.getName(), userDtoRequest.getEmail());
        requester = new User(userDtoRequest1.getId(), userDtoRequest1.getName(), userDtoRequest1.getEmail());
        itemRequest1 = new ItemRequest(1L, "description for request 1", requester, now, null);
        item1 = new Item(1L, "name for item 1", "description for item 1", true, owner1, null);
        requestDto = new ItemRequestDto(1L, item1.getDescription(), now, List.of());
    }

/*
        requestResponseDto = ItemRequestResponseDto.builder()
                .description(item1.getDescription())
                .requester(new UserForItemRequestDto(requester.getId(), requester.getName()))
                .created(now)
                .items(List.of())
                .build();


 */

    @SneakyThrows
    @Test
    void addItemRequestTest() {
        UserDto savedOwnerDto1 = userService.createUser(userDtoRequest);
        query = em.createQuery("Select ir from ItemRequest ir", ItemRequest.class);
        List<ItemRequest> requestsOfEmpty = query.getResultList();

        assertEquals(0, requestsOfEmpty.size());

        ItemRequestDto saveItemRequest =
                itemRequestService.createItemRequest(savedOwnerDto1.getId(), requestDto);
        List<ItemRequest> afterSave = query.getResultList();

        assertNotNull(afterSave);
        assertEquals(1, afterSave.size());
        assertEquals(saveItemRequest.getId(), afterSave.get(0).getId());
        assertEquals(saveItemRequest.getCreated(), afterSave.get(0).getCreated());
        assertEquals(saveItemRequest.getDescription(), afterSave.get(0).getDescription());
    }

    @Test
    void addItemRequestWhenRequesterIdIsNullReturnNotFoundRecordInBDTest() {
        Long requesterId = 9991L;
        assertThrows(NotFoundException.class,
                () -> itemRequestService.createItemRequest(requesterId, requestDto));
    }

    @SneakyThrows
    @Test
    void getItemRequestsByUserIdTest() {
        UserDto savedUserDto = userService.createUser(userDtoRequest1);
        ItemRequestDto savedItemRequest =
                itemRequestService.createItemRequest(savedUserDto.getId(), requestDto);

        query = em.createQuery("Select ir from ItemRequest ir", ItemRequest.class);

        List<ItemRequestDto> itemsFromDb =
                itemRequestService.findUserItemRequests(savedUserDto.getId());

        assertNotNull(itemsFromDb);
        assertEquals(1, itemsFromDb.size());
        assertEquals(savedItemRequest.getId(), itemsFromDb.get(0).getId());
        assertEquals(savedItemRequest.getCreated(), itemsFromDb.get(0).getCreated());
    }

    @SneakyThrows
    @Test
    void getAllItemRequestsTest() {
        UserDto saveRequesterDto = userService.createUser(userDtoRequest1);
        UserDto saveOwnerDto = userService.createUser(userDtoRequest);

        ItemRequestDto savedItemRequest =
                itemRequestService.createItemRequest(saveRequesterDto.getId(), requestDto);

        query = em.createQuery("Select r from ItemRequest r where r.requestor.id <> :userId", ItemRequest.class);
        List<ItemRequest> itemRequestList = query.setParameter("userId", saveOwnerDto.getId())
                .getResultList();

        List<ItemRequestDto> emptyItemsFromDbForRequester =
                itemRequestService.findRequestsAnotherUsers(saveRequesterDto.getId(), 0, 5);

        assertEquals(0, emptyItemsFromDbForRequester.size());

        List<ItemRequestDto> oneItemFromDbForOwner =
                itemRequestService.findRequestsAnotherUsers(saveOwnerDto.getId(), 0, 1);

        assertNotNull(oneItemFromDbForOwner);
        assertEquals(savedItemRequest.getId(), oneItemFromDbForOwner.get(0).getId());
        assertEquals(savedItemRequest.getDescription(), oneItemFromDbForOwner.get(0).getDescription());
        assertEquals(List.of(), oneItemFromDbForOwner.get(0).getItems());
        assertEquals(savedItemRequest.getCreated(), oneItemFromDbForOwner.get(0).getCreated());
    }

    @SneakyThrows
    @Test
    void getItemRequestTest() {
        UserDto savedRequesterDto = userService.createUser(userDtoRequest1);
        UserDto alien = userService.createUser(new UserDto(null, "alien", "mail@ru"));
        ItemRequestDto savedItRequest =
                itemRequestService.createItemRequest(savedRequesterDto.getId(), requestDto);
        ItemRequestDto requestFromDb =
                itemRequestService.findOneItemRequest(alien.getId(), savedItRequest.getId());
        assertNotNull(requestFromDb);
        assertEquals(savedItRequest.getId(), requestFromDb.getId());
        assertEquals(savedItRequest.getCreated(), requestFromDb.getCreated());
        assertEquals(savedItRequest.getDescription(), requestFromDb.getDescription());

    }
}
/*
@Slf4j

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "classpath:data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class RequestServiceImplTest {

    private final EntityManager em;

    private final RequestService requestService;

    private final ItemService itemService;

    private final UserService userService;

    private ItemDto itemDto;
    private UserDto userDto;
    private UserDto userDto2;
    private ItemRequestDto requestDto;

    @BeforeEach
    void beforeEach() {
        itemDto = new ItemDto(1L, "Вещь 1", "Описание вещи 1", true, 1L);
        userDto = new UserDto(1L, "Иван Иванович", "ii@mail.ru");
        userDto2 = new UserDto(2L, "Петр Петрович", "pp@mail.ru");
        requestDto = new ItemRequestDto(1L, "Описание запроса 1", LocalDateTime.now(), null);

        userService.createUser(userDto);
        userService.createUser(userDto2);
    }

    @Test
    void createItemRequest() {

        ItemRequestDto methodRequest = requestService.createItemRequest(userDto2.getId(), requestDto);

        TypedQuery<ItemRequest> query = em.createQuery("select r from ItemRequest r where r.id = :id", ItemRequest.class);
        ItemRequest dbRequest = query.setParameter("id", requestDto.getId())
                .getSingleResult();

        assertThat(methodRequest.getId(), equalTo(dbRequest.getId()));
        assertThat(methodRequest.getDescription(), equalTo(dbRequest.getDescription()));
        assertThat(methodRequest.getCreated(), notNullValue());
        assertThat(methodRequest.getItems(), notNullValue());
    }

    @Test
    void createItemRequestWithWrongUserId() {
        long userId = 3L;

        try {
            requestService.createItemRequest(userId, requestDto);
        } catch (NotFoundException thrown) {
            assertThat(thrown.getMessage(), equalTo("Пользователь с id " + userId + " не зарегистрирован"));
        }
    }

    @Test
    void getItemRequestsFromOtherUsersIfUserIsRequester() {
        int from = 0;
        int size = 5;
        requestService.createItemRequest(userDto2.getId(), requestDto); // создали запрос на вещь 1 от User 2
        itemService.createItem(userDto.getId(), itemDto);

        List<ItemRequestDto> methodRequestList = requestService.findRequestsAnotherUsers(userDto2.getId(), from, size);

        assertThat(methodRequestList.size(), equalTo(0));
    }

}

 */
