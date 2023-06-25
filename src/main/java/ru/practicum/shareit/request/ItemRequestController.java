package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestResponseDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Создать запрос на добавление вещи с описанием '{}' от пользователя с ID:{}",
                itemRequestDto.getDescription(),
                userId);
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestResponseDto> findAllRequestsByRequestor(@RequestHeader(
            "X-Sharer-User-Id") Long userId) {
        log.info("Поиск всех запросов на добавление вещей пользователя с ID:{}", userId);
        return itemRequestService.findAllRequestsByRequestor(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> findAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                        @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Поиск всех запросов на добавление вещей");
        return itemRequestService.findAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto findRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PathVariable Long requestId) {
        log.info("Поиск запроса на добавление вещи под ID:{}", requestId);
        return itemRequestService.findRequestById(userId, requestId);
    }

}
