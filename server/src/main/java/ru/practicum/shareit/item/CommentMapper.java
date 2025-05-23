package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static Comment dtoToComment(CommentDto comment, Item item, User user) {
        return new Comment(
                comment.getId(),
                comment.getText(),
                item,
                user,
                LocalDateTime.now()
        );
    }

    public static CommentDto commentToDto(Comment comment) {

        return new CommentDto(
                comment.getId(),
                comment.getText(),
                ItemMapper.itemToDto(comment.getItem()),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static List<CommentDto> commentToDtoList(List<Comment> commentList) {
        return commentList.stream()
                .map(CommentMapper::commentToDto)
                .collect(Collectors.toList());
    }

}