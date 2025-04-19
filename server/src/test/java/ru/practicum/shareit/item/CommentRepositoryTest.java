package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.data.domain.Sort.Direction.DESC;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CommentRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    RequestRepository itemRequestRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CommentRepository commentRepository;
    User user1;
    User user2;
    Item item3;
    Comment comment;

    @BeforeEach
    void setUp() {

        user1 = userRepository.save(new User(1L, "user1", "user@mail.ru"));
        itemRepository.save(new Item(1L, "item1", "description item1", true, user1, null));
        itemRepository.save(new Item(2L, "item2", "description item2", true, user1, null));
        user2 = userRepository.save(new User(2L, "user2", "user2@mail.ru"));
        ItemRequest itemRequest = itemRequestRepository.save(new ItemRequest(1L,
                "description for request 1", user2, LocalDateTime.now(), null));
        item3 = itemRepository.save(new Item(3L, "item3", "description item3", true, user1, null));
        itemRequest.setItems(List.of(item3));
        comment = commentRepository.save(new Comment(1L, "text", item3, user2, LocalDateTime.now()));
    }

    @Test
    void findAllByOwnerOrderByIdTest() {
        List<Comment> commentList = commentRepository.findByItemIn(List.of(item3), Sort.by(DESC, "created"));

        assertNotNull(commentList);
        assertEquals(1, commentList.size());
        assertEquals(item3, commentList.get(0).getItem());
        assertEquals(comment.getAuthor(), commentList.get(0).getAuthor());
        assertEquals(comment.getText(), commentList.get(0).getText());
        assertEquals(comment.getCreated(), commentList.get(0).getCreated());
    }
}