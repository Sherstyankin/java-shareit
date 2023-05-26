package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ModelMapper modelMapper = new ModelMapper();

    @GetMapping
    public List<ItemDto> findAllUserItems(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return itemService.findAllUserItems(userId).stream()
                .map(item -> modelMapper.map(item, ItemDto.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@PathVariable @NotNull Long itemId) {
        return modelMapper.map(itemService.findById(itemId), ItemDto.class);
    }

    @GetMapping("/search")
    public List<ItemDto> findByText(@RequestParam String text) {
        return itemService.findByText(text).stream()
                .map(item -> modelMapper.map(item, ItemDto.class))
                .collect(Collectors.toList());
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                          @RequestBody @Valid ItemDto item) {
        return modelMapper.map(itemService.create(userId, modelMapper.map(item, Item.class)), ItemDto.class);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                          @RequestBody ItemDto item,
                          @PathVariable @NotNull Long itemId) {
        return modelMapper.map(itemService.update(userId, modelMapper.map(item, Item.class), itemId), ItemDto.class);
    }
}
