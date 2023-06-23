package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final ModelMapper modelMapper;

    @Override
    public List<UserDto> findAll() {
        log.info("Получаем список всех пользователей.");
        return repository.findAll().stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        log.info("Добавляем следующего пользователя: {}", user);
        return modelMapper.map(repository.save(user), UserDto.class);
    }

    @Override
    public UserDto update(UserDto userDto, Long userId) {
        User userToUpdate = findUser(userId);
        if (userDto.getEmail() != null
                && !userDto.getEmail().isEmpty()
                && !Objects.equals(userDto.getEmail(), userToUpdate.getEmail())) {
            userToUpdate.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null
                && !userDto.getName().isEmpty()) {
            userToUpdate.setName(userDto.getName());
        }
        log.info("Обновляем следующего пользователя: {}", userToUpdate);
        return modelMapper.map(repository.save(userToUpdate), UserDto.class);
    }

    @Override
    public UserDto findById(Long userId) {
        log.info("Получаем пользователя с ID:{}", userId);
        User user = findUser(userId);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public void delete(Long userId) {
        User userTodelete = findUser(userId);
        log.info("Удаляем пользователя под ID: {}", userId);
        repository.delete(userTodelete);
    }

    private User findUser(Long userId) {
        return repository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Пользователь c ID:" +
                        userId + " не существует!", User.class));
    }
}
