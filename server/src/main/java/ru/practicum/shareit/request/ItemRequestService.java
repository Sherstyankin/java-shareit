package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestResponseDto create(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestResponseDto> findAllRequestsByRequestor(Long requestorId);

    List<ItemRequestResponseDto> findAllRequests(Long userId, Integer from, Integer size);

    ItemRequestResponseDto findRequestById(Long userId, Long requestId);
}
