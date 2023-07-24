package ru.practicum.ewm.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.NewUserRequest;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.mapper.UserMapper.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserServiceImpl implements AdminUserService {
    private final UserRepository repository;

    @Override
    public List<UserDto> getAll(List<Long> ids, Integer from, Integer size) {
        if (ids == null) {
            return toUserDtoList(repository.findAll().stream().skip(from).limit(size).collect(Collectors.toList()));
        } else {
            return toUserDtoList(repository.findAllById(ids));
        }
    }

    @Transactional
    @Override
    public UserDto save(NewUserRequest newUserRequest) {
        if (newUserRequest.getName() == null) {
            throw new BadRequestException("Field: name. Error: must not be blank. Value: null");
        }
        return toUserDto(repository.save(toUser(newUserRequest)));
    }

    @Transactional
    @Override
    public void delete(Long userId) {
        repository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));
        repository.deleteById(userId);
    }
}
