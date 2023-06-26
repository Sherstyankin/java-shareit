package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.User;

import java.util.List;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequestResponseDto mapToItemRequestResponseDto(ItemRequest itemRequest,
                                                              List<ItemForItemRequestDto> itemDtos) {
        return ItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemDtos)
                .build();
    }

    public ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto, User requestor) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requestor(requestor)
                .build();
    }
}
