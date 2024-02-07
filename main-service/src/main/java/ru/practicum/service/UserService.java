package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.db.UserRepository;
import ru.practicum.dto.UserDto.NewUserRequest;
import ru.practicum.dto.UserDto.UserDto;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public List<UserDto> getList(Long[] ids, Integer from, Integer size) {
        if (ids == null || ids.length == 0) {
            PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
            return userRepository.findAll(pageRequest).stream().map(userMapper::entityToDto)
                    .collect(Collectors.toList());
        } else {
            log.info("list users = {}", userRepository.findAllByIdIn(ids).stream().map(userMapper::entityToDto)
                    .collect(Collectors.toList()));
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
