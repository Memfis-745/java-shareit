package ru.practicum.shareit.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.dtoToUser(userDto);
        User userSaved = repository.save(user);
        log.info("Пользователь создан с Id : {}", userSaved.getId());
        return UserMapper.userToDto(userSaved);
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        User userFromDto = UserMapper.dtoToUser(userDto, user);
        userFromDto.setId(userId);
        return UserMapper.userToDto(repository.save(userFromDto));
    }


    @Override
    public UserDto getUserById(long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        return UserMapper.userToDto(user);
    }

    @Override
    public void deleteUser(long userId) {
        repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        repository.deleteById(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> usersList = repository.findAll();
        return usersList.stream()
                .map(UserMapper::userToDto)
                .collect(Collectors.toList());
    }
}
