package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User(0L, "user1", "user@mail.ru"));
        itemRepository.save(new Item(0L, "item1", "description item1", true, user, null));
        itemRepository.save(new Item(0L, "item2", "description item2", true, user, null));
    }

    @Test
    void findAllByOwnerOrderByIdTest() {
        List<Item> itemList = itemRepository
                .findAllByOwnerIdOrderByItemId(user.getId(), PageRequest.of(0, 2)).getContent();

        assertNotNull(itemList);
        assertEquals(2, itemList.size());
    }

    @Test
    void searchItemsByTextTest() {
        //  Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());
        List<Item> itemList =
                itemRepository.findByNameOrDescription("it");

        assertNotNull(itemList);
        assertEquals(2, itemList.size());
    }

    @Test
    public void getAllItemsWithBlankTextShouldReturnEmptyListTest() {
        String text = "text";
        Pageable page = PageRequest.of(0, 10);

        List<Item> actualResult = itemRepository.findByNameOrDescription(text);
        assertEquals(List.of(), actualResult);
    }

    @Test
    public void findByRequestIdInTest() {
        List<Item> actualResult = itemRepository.findAllByRequestId(user.getId());

        assertNotNull(actualResult);
        assertEquals(0, actualResult.size());
    }

    @Test
    public void findAllByRequestIdTest() {
        itemRepository.save(new Item(0L, "item1", "description item1", true, user, null));
        List<Item> actualResult = itemRepository.findAllByRequestId(user.getId());

        assertNotNull(actualResult);
        assertEquals(0, actualResult.size());
    }
}