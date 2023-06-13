package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.comment.ResponseCommentDto;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseItemDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingForItemDto lastBooking;

    private BookingForItemDto nextBooking;

    private List<ResponseCommentDto> comments;
}
