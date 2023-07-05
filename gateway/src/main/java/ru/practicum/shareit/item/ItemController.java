package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.RequestCommentDto;
import ru.practicum.shareit.item.dto.RequestItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> findAllOwnerItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                    @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получен запрос на поиск всех вещей пользователя(владельца) с ID:{}", userId);
        return itemClient.findAllOwnerItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long itemId) {
        log.info("Получен запрос на поиск вещи с ID:{} от пользователя с ID:{}", itemId, userId);
        return itemClient.findById(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findByText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam String text,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                             @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получен запрос на поиск вещи по тексту '{}'", text);
        return itemClient.findByText(userId, text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody @Valid RequestItemDto itemDto) {
        log.info("Получен запрос на добавление вещи с названием:'{}' от пользователя с ID:{}",
                itemDto.getName(),
                userId);
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody RequestItemDto itemDto,
                                         @PathVariable Long itemId) {
        log.info("Получен запрос на редактирование вещи с ID:{} от пользователя с ID:{}",
                itemId,
                userId);
        return itemClient.update(userId, itemDto, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody @Valid RequestCommentDto commentDto,
                                             @PathVariable Long itemId) {
        log.info("Получен запрос на добавление отзыва для вещи с ID:{} от пользователя с ID:{}",
                itemId,
                userId);
        return itemClient.addComment(userId, commentDto, itemId);
    }
}
