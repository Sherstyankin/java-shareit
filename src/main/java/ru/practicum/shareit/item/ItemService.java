package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;

import java.util.List;

public interface ItemService {
    List<ResponseItemDto> findAllOwnerItems(Long userId);

    ResponseItemDto findById(Long userId, Long itemId);

    List<Item> findByText(String text);

    Item create(Long userId, Item item);

    Item update(Long userId, Item itemToUpdate, Long itemId);

    ResponseCommentDto addComment(Long userId, Comment comment, Long itemId);
}
