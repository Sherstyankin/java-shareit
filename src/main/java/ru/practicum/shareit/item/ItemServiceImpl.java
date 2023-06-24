package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.UserNotBookerOrBookingNotFinishedException;
import ru.practicum.shareit.exception.UserNotOwnerOrBookerException;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final ItemRequestRepository itemRequestRepository;

    private final ModelMapper modelMapper;

    @Override
    public List<ResponseItemDto> findAllOwnerItems(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Item> items = itemRepository.findByOwnerIdOrderByIdAsc(userId, pageable);
        if (!items.isEmpty()) {
            checkOwner(userId, items.get(0).getOwner().getId());
        } else {
            return Collections.emptyList();
        }
        // достаем все комментарии и распределяем по группам(вещам)
        Map<Item, List<Comment>> comments = commentRepository
                .findByItemIn(items, Sort.by(DESC, "created")).stream()
                .collect(groupingBy(Comment::getItem));
        // достаем все следующие бронирования и распределяем по группам(вещам)
        Map<Item, List<Booking>> next = bookingRepository.findNextBookingForAllOwnerItems(userId).stream()
                .collect(groupingBy(Booking::getItem));
        // достаем все предыдущие бронирования и распределяем по группам(вещам)
        Map<Item, List<Booking>> last = bookingRepository.findLastBookingForAllOwnerItems(userId).stream()
                .collect(groupingBy(Booking::getItem));
        // собираем список из dto-объектов
        List<ResponseItemDto> output = items.stream()
                .map(item -> {
                    List<Booking> nextBookingList = next.getOrDefault(item, Collections.emptyList());
                    List<Booking> lastBookingList = last.getOrDefault(item, Collections.emptyList());
                    BookingForItemDto lastBookingDto = BookingMapper
                            .mapToBookingForItemDto(lastBookingList.stream()
                                    .findFirst().orElse(null));
                    BookingForItemDto nextBookingDto = BookingMapper
                            .mapToBookingForItemDto(lastBookingList.isEmpty() ? null : nextBookingList.stream()
                                    .findFirst().orElse(null));
                    List<ResponseCommentDto> commentDtos = CommentMapper
                            .mapToResponseCommentDto(comments.getOrDefault(item, Collections.emptyList()));
                    return ItemMapper.mapToResponseItemDto(item,
                            lastBookingDto,
                            nextBookingDto,
                            commentDtos);
                })
                .collect(Collectors.toList());
        log.info("Получаем все вещи пользователя с ID:{}", userId);
        return output;
    }

    @Override
    public ResponseItemDto findById(Long userId, Long itemId) {
        Item item = findItem(itemId);
        log.info("Получаем вещь с ID:{}", itemId);
        List<Comment> comments = commentRepository.findByItemId(itemId);
        List<ResponseCommentDto> commentDtos = CommentMapper
                .mapToResponseCommentDto(comments);
        if (Objects.equals(userId, item.getOwner().getId())) {
            List<Booking> next = bookingRepository.findNextBookingForItem(itemId);
            List<Booking> last = bookingRepository.findLastBookingForItem(itemId);
            BookingForItemDto lastBookingDto = BookingMapper
                    .mapToBookingForItemDto(last.stream().findFirst().orElse(null));
            BookingForItemDto nextBookingDto = BookingMapper
                    .mapToBookingForItemDto(last.isEmpty() ? null : next.stream()
                            .findFirst().orElse(null));
            return ItemMapper.mapToResponseItemDto(item, lastBookingDto, nextBookingDto, commentDtos);
        } else {
            return ItemMapper.mapToResponseItemDto(item, null, null, commentDtos);
        }
    }

    @Override
    public List<ItemDto> findByText(String text, Integer from, Integer size) {
        if (text.isBlank()) {
            log.info("Возвращаем пустой список, так как текст запроса не указан.");
            return Collections.emptyList();
        }
        log.info("Возвращаем список вещей, который соответствует тексту запроса: '{}'", text);
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Item> items = itemRepository.findByText(text, pageable);
        return items.stream()
                .map(item -> modelMapper.map(item, ItemDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        Item item = modelMapper.map(itemDto, Item.class);
        item.setOwner(findUser(userId));
        if (itemDto.getRequestId() != null) {
            item.setItemRequest(findItemRequest(itemDto.getRequestId()));
        }
        log.info("Добавляем новую вещь: {}", item);
        return modelMapper.map(itemRepository.save(item), ItemDto.class);
    }

    @Override
    public ItemDto update(Long userId, ItemDto itemDto, Long itemId) {
        Item item = modelMapper.map(itemDto, Item.class);
        Item itemToUpdate = findItem(itemId);
        checkOwner(userId, itemToUpdate.getOwner().getId());
        if (item.getName() != null && !item.getName().isBlank()) {
            log.info("Редактируем название вещи.");
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            log.info("Редактируем описание вещи.");
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            log.info("Изменяем доступность вещи.");
            itemToUpdate.setAvailable(item.getAvailable());
        }
        return modelMapper.map(itemRepository.save(itemToUpdate), ItemDto.class);
    }

    public ResponseCommentDto addComment(Long userId, RequestCommentDto commentDto, Long itemId) {
        Comment comment = modelMapper.map(commentDto, Comment.class);
        // проверить то, что пользователь брал вещь в аренду и аренда завершена
        Boolean isBookerAndFinished = bookingRepository.checkIsBookerAndFinished(userId, itemId);
        if (isBookerAndFinished) {
            Item item = findItem(itemId);
            User author = findUser(userId);
            comment.setItem(item);
            comment.setAuthor(author);
            return CommentMapper.mapToResponseCommentDto(commentRepository.save(comment));
        } else {
            throw new UserNotBookerOrBookingNotFinishedException("Пользователь с ID:" + userId +
                    " еще не брал вещь в аренду или аренда не завершена.");
        }
    }

    private void checkOwner(Long userId, Long ownerId) {
        if (!Objects.equals(userId, ownerId)) {
            log.warn("Пользователь c ID={} не является владельцем вещи!", userId);
            throw new UserNotOwnerOrBookerException("Пользователь c ID:" + userId +
                    " не является владельцем вещи!");
        }
    }

    private Item findItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещь c ID:" +
                        itemId + " не существует!", Item.class));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Пользователь c ID:" +
                        userId + " не существует!", User.class));
    }

    private ItemRequest findItemRequest(Long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос на вещь c ID:" +
                        requestId + " не найден!", ItemRequest.class));
    }
}
