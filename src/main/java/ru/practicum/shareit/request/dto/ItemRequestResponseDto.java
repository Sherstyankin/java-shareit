package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.configuration.ApplicationConfig.TIME_FORMAT;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestResponseDto {

    private Long id;

    private String description;

    @JsonFormat(pattern = TIME_FORMAT)
    private LocalDateTime created;

    @Builder.Default
    private List<ItemForItemRequestDto> items = new ArrayList<>();
}
