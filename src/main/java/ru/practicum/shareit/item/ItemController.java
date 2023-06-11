package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.RequestCommentDto;
import ru.practicum.shareit.item.comment.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ModelMapper modelMapper;

    @GetMapping
    public List<ResponseItemDto> findAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.findAllUserItems(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseItemDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId) {
        return itemService.findById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> findByText(@RequestParam String text) {
        return itemService.findByText(text).stream()
                .map(item -> modelMapper.map(item, ItemDto.class))
                .collect(Collectors.toList());
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody @Valid ItemDto item) {
        return modelMapper.map(itemService.create(userId, modelMapper.map(item, Item.class)), ItemDto.class);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto item,
                          @PathVariable Long itemId) {
        return modelMapper.map(itemService.update(userId, modelMapper.map(item, Item.class), itemId),
                ItemDto.class);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseCommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody @Valid RequestCommentDto commentDto,
                                         @PathVariable Long itemId) {
        Comment comment = modelMapper.map(commentDto, Comment.class);
        return itemService.addComment(userId, comment, itemId);
    }
}
