package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.RequestCommentDto;
import ru.practicum.shareit.item.comment.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;

import java.util.List;

public interface ItemService {
    List<ResponseItemDto> findAllOwnerItems(Long userId, Integer from, Integer size);

    ResponseItemDto findById(Long userId, Long itemId);

    List<ItemDto> findByText(Long userId, String text, Integer from, Integer size);

    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, ItemDto itemDto, Long itemId);

    ResponseCommentDto addComment(Long userId, RequestCommentDto comment, Long itemId);
}
