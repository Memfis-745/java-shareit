package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.dtoToUser(userDto);
        User userSaved = repository.addUser(user);
        return userMapper.userToDto(userSaved);
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {

        User userFromDto = userMapper.dtoToUser(userDto);
        return userMapper.userToDto(repository.updateUser(userFromDto, userId));
    }


    @Override
    public UserDto getUserById(long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        return userMapper.userToDto(user);
    }

    @Override
    public void deleteUser(long userId) {
        repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        repository.deleteById(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        Collection<User> usersList = repository.getAll();
        return usersList.stream()
                .map(userMapper::userToDto)
                .collect(Collectors.toList());
    }
}
