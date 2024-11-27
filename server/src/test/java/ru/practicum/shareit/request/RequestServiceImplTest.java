package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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
