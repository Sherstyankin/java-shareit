package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.RequestCommentDto;
import ru.practicum.shareit.item.comment.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ResponseItemDto> findAllOwnerItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam Integer from,
                                                   @RequestParam Integer size) {
        log.info("Получен запрос на поиск всех вещей пользователя(владельца) с ID:{}", userId);
        return itemService.findAllOwnerItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseItemDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId) {
        log.info("Получен запрос на поиск вещи с ID:{} от пользователя с ID:{}", itemId, userId);
        return itemService.findById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> findByText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestParam String text,
                                    @RequestParam Integer from,
                                    @RequestParam Integer size) {
        log.info("Получен запрос на поиск вещи по тексту '{}'", text);
        return itemService.findByText(userId, text, from, size);
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на добавление вещи с названием:'{}' от пользователя с ID:{}",
                itemDto.getName(),
                userId);
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto,
                          @PathVariable Long itemId) {
        log.info("Получен запрос на редактирование вещи с ID:{} от пользователя с ID:{}",
                itemId,
                userId);
        return itemService.update(userId, itemDto, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseCommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody RequestCommentDto commentDto,
                                         @PathVariable Long itemId) {
        log.info("Получен запрос на добавление отзыва для вещи с ID:{} от пользователя с ID:{}",
                itemId,
                userId);
        return itemService.addComment(userId, commentDto, itemId);
    }
}
