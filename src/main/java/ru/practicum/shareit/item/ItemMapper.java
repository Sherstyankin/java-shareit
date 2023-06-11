package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.comment.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;

import java.util.List;

public class ItemMapper {
    private ItemMapper() {
    }

    public static ResponseItemDto mapToResponseItemDto(Item item,
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
                .build();
    }
}
