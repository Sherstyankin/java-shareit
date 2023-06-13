package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @GetMapping
    public List<UserDto> findAll() {
        log.info("Получен запрос на получение всех пользователей");
        return userService.findAll().stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable Long userId) {
        log.info("Получен запрос на получение пользователя c ID:{}", userId);
        return modelMapper.map(userService.findById(userId), UserDto.class);
    }

    @PostMapping
    public UserDto create(@RequestBody @Validated(Create.class) UserDto user) {
        log.info("Получен запрос на создание пользователя c именем: '{}'", user.getName());
        return modelMapper.map(userService.create(modelMapper.map(user, User.class)), UserDto.class);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody @Validated(Update.class) UserDto user,
                          @PathVariable Long userId) {
        log.info("Получен запрос на редактирование пользователя c ID:{}", userId);
        return modelMapper.map(userService.update(modelMapper.map(user, User.class), userId), UserDto.class);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("Получен запрос на удаление пользователя c ID:{}", userId);
        userService.delete(userId);
    }
}
