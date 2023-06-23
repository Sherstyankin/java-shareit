package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.comment.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ItemMapper {
    public ResponseItemDto mapToResponseItemDto(Item item,
                                                BookingForItemDto lastBooking,
                                                BookingForItemDto nextBooking,
                                                List<ResponseCommentDto> commentDtos) {
        return ResponseItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(commentDtos)
                .requestId(item.getItemRequest() != null ? item.getItemRequest().getId() : null)
                .build();
    }

    public ItemForItemRequestDto mapToItemForItemRequestDto(Item item) {
        return ItemForItemRequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getItemRequest().getId())
                .build();
    }

    public List<ItemForItemRequestDto> mapToItemForItemRequestDto(Iterable<Item> items) {
        List<ItemForItemRequestDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(mapToItemForItemRequestDto(item));
        }
        return dtos;
    }
}
