package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper = new ModelMapper();

    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll().stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable @NotNull Long userId) {
        return modelMapper.map(userService.findById(userId), UserDto.class);
    }

    @PostMapping
    public UserDto create(@RequestBody @Valid UserDto user) {
        return modelMapper.map(userService.create(modelMapper.map(user, User.class)), UserDto.class);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto user,
                          @PathVariable @NotNull Long userId) {
        return modelMapper.map(userService.update(modelMapper.map(user, User.class), userId), UserDto.class);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable @NotNull Long userId) {
        userService.delete(userId);
    }
}
