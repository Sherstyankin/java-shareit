package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> findAll() {
        log.info("Получен запрос на получение всех пользователей");
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable Long userId) {
        log.info("Получен запрос на получение пользователя c ID:{}", userId);
        return userService.findById(userId);
    }

    @PostMapping
    public UserDto create(@RequestBody @Validated(Create.class) UserDto userDto) {
        log.info("Получен запрос на создание пользователя c именем: '{}'", userDto.getName());
        return userService.create(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody @Validated(Update.class) UserDto userDto,
                          @PathVariable Long userId) {
        log.info("Получен запрос на редактирование пользователя c ID:{}", userId);
        return userService.update(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("Получен запрос на удаление пользователя c ID:{}", userId);
        userService.delete(userId);
    }
}
