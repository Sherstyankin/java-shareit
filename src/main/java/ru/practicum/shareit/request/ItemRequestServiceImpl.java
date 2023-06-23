package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    @Override
    public ItemRequestResponseDto create(Long userId, ItemRequestDto itemRequestDto) {
        User requestor = findUser(userId);
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(itemRequestDto, requestor);
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        return modelMapper.map(savedItemRequest, ItemRequestResponseDto.class);
    }

    @Override
    public List<ItemRequestResponseDto> findAllRequestsByRequestor(Long requestorId) {
        checkIsUserExists(requestorId);
        List<ItemRequest> requests = itemRequestRepository
                .findAllByRequestorIdOrderByCreatedDesc(requestorId);
        return attachItemsToRequestAndMapToDto(requests);
    }

    @Override
    public List<ItemRequestResponseDto> findAllRequests(Long userId, Integer from, Integer size) {
        checkIsUserExists(userId);
        List<ItemRequest> requests;
        if (from == null || size == null) {
            requests = itemRequestRepository
                    .findAllByRequestorIdNotOrderByCreatedDesc(userId);
        } else {
            Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);
            requests = itemRequestRepository
                    .findAllByRequestorIdNotOrderByCreatedDesc(userId, pageable).getContent();
        }
        return attachItemsToRequestAndMapToDto(requests);
    }

    @Override
    public ItemRequestResponseDto findRequestById(Long userId, Long requestId) {
        checkIsUserExists(userId);
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос на вещь c ID:" +
                        requestId + " не найден!", ItemRequest.class));
        List<Item> items = itemRepository.findByItemRequest(request, Sort.by(ASC, "id"));
        List<ItemForItemRequestDto> itemDtos = ItemMapper.mapToItemForItemRequestDto(items);
        return ItemRequestMapper.mapToItemRequestResponseDto(request,
                itemDtos);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Пользователь c ID:" +
                        userId + " не существует!", User.class));
    }

    private void checkIsUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Пользователь c ID:" +
                    userId + " не существует!", User.class);
        }
    }

    private List<ItemRequestResponseDto> attachItemsToRequestAndMapToDto(List<ItemRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return Collections.emptyList();
        }
        // получить все вещи и распределить по группам (запрос на вещь)
        Map<ItemRequest, List<Item>> items = itemRepository
                .findByItemRequestIn(requests, Sort.by(ASC, "id")).stream()
                .collect(groupingBy(Item::getItemRequest));
        // привязать лист вещей к ItemRequestWithResponseDto
        return requests.stream()
                .map(request -> {
                    List<Item> itemList = items.getOrDefault(request, Collections.emptyList());
                    List<ItemForItemRequestDto> itemDtos = ItemMapper.mapToItemForItemRequestDto(itemList);
                    return ItemRequestMapper.mapToItemRequestResponseDto(request,
                            itemDtos);
                })
                .collect(Collectors.toList());
    }
}
