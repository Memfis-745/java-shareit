package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class CommentTest {
    Comment comment1 = new Comment(1L, "text1", null, null, null);
    Comment comment2 = new Comment(1L, "text1", null, null, null);
    Comment comment3 = new Comment(3L, "text3", null, null, null);

    @Test
    void testCommentHashCode() {
        assertNotEquals(comment1, comment2);
        assertNotEquals(comment1, comment3);
    }

}