package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.UserNotBookerOrBookingNotFinished;
import ru.practicum.shareit.exception.UserNotOwnerOrBookerException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
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

    @Override
    public List<ResponseItemDto> findAllOwnerItems(Long userId) {
        List<Item> items = itemRepository.findByOwnerIdOrderByIdAsc(userId);
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
    public List<Item> findByText(String text) {
        if (text.isBlank()) {
            log.info("Возвращаем пустой список, так как текст запроса не указан.");
            return Collections.emptyList();
        } else {
            log.info("Возвращаем список вещей, который соответствует тексту запроса: '{}'", text);
            return itemRepository.findByText(text);
        }
    }

    @Override
    public Item create(Long userId, Item item) {
        item.setOwner(findUser(userId));
        log.info("Добавляем новую вещь: {}", item);
        return itemRepository.save(item);
    }

    @Override
    public Item update(Long userId, Item item, Long itemId) {
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
        return itemRepository.save(itemToUpdate);
    }

    public ResponseCommentDto addComment(Long userId, Comment comment, Long itemId) {
        // проверить то, что пользователь брал вещь в аренду и аренда завершена
        Boolean isBookerAndFinished = bookingRepository.checkIsBookerAndFinished(userId, itemId);
        if (isBookerAndFinished) {
            Item item = findItem(itemId);
            User author = findUser(userId);
            comment.setItem(item);
            comment.setAuthor(author);
            return CommentMapper.mapToResponseCommentDto(commentRepository.save(comment));
        } else {
            throw new UserNotBookerOrBookingNotFinished("Пользователь с ID:" + userId +
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
}
