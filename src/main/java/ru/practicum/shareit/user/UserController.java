package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
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
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll().stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable Long userId) {
        return modelMapper.map(userService.findById(userId), UserDto.class);
    }

    @PostMapping
    public UserDto create(@RequestBody @Validated(Create.class) UserDto user) {
        return modelMapper.map(userService.create(modelMapper.map(user, User.class)), UserDto.class);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody @Validated(Update.class) UserDto user,
                          @PathVariable Long userId) {
        return modelMapper.map(userService.update(modelMapper.map(user, User.class), userId), UserDto.class);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
