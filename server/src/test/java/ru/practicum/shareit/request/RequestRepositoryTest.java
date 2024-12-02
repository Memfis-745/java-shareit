package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RequestRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    RequestRepository itemRequestRepository;
    @Autowired
    UserRepository userRepository;
    User user1;
    User user2;

    @BeforeEach
    void setUp() {

        user1 = userRepository.save(new User(0L, "user1", "user@mail.ru"));
        user2 = userRepository.save(new User(0L, "user2", "user2@mail.ru"));
        itemRepository.save(new Item(0L, "item", "description item", true, user1, null));
        itemRepository.save(new Item(0L, "item2", "description item2", true, user1, null));

        userRepository.save(user2);
        itemRequestRepository.save(new ItemRequest(0L, "description", user2, LocalDateTime.now(), null));
    }

    @Test
    void findAllByNotRequesterIdTest() {
        List<ItemRequest> itemRequests = itemRequestRepository
                .findAllByRequestor_IdNot(user1.getId(), PageRequest.of(0, 2)).getContent();

        assertNotNull(itemRequests);
        assertEquals(1, itemRequests.size());
    }

    @Test
    void findAllTest() {
        List<ItemRequest> itemRequests = itemRequestRepository
                .findAll(PageRequest.of(0, 2)).getContent();

        assertNotNull(itemRequests);
        assertEquals(1, itemRequests.size());
    }

    @Test
    public void findItemRequestsByUserIdTest() {
        List<ItemRequest> itemRequests = itemRequestRepository
                .findAllByRequestor_IdOrderByCreatedDesc(user2.getId());

        assertNotNull(itemRequests);
        assertEquals(1, itemRequests.size());
    }
}