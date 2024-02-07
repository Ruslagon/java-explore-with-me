package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.db.UserRepository;
import ru.practicum.dto.UserDto.NewUserRequest;
import ru.practicum.dto.UserDto.UserDto;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public List<UserDto> getList(Long[] ids, Integer from, Integer size) {
        if (ids == null || ids.length == 0) {
            PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
            return userRepository.findAll(pageRequest).stream().map(userMapper::entityToDto)
                    .collect(Collectors.toList());
        } else {
            return userRepository.findAllByIdIn(ids).stream().map(userMapper::entityToDto)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public UserDto add(NewUserRequest newUserRequest) {
        User userSaved = userRepository.save(userMapper.newToEntity(newUserRequest));

        return userMapper.entityToDto(userSaved);
    }

    @Transactional
    public void delete(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id=" + userId + " was not found"));

        userRepository.deleteById(userId);
    }
}
