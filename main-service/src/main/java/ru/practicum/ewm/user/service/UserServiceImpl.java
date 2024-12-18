package ru.practicum.ewm.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.data.OffsetBasedPageRequest;
import ru.practicum.ewm.exception.ConditionsNotRespected;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.AdminUserParam;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.user.dto.CreateUserDto;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private static final String USER_EXISTS_BY_EMAIL = "Пользователь с почтой %s уже существует!";

    private static final String NOT_EXISTING_USER = "Пользователь c id %s не найден";

    @Override
    @Transactional
    public User create(CreateUserDto userDto) {
        log.info("ADMIN: Запрос на создание пользователя {}", userDto);

        User user = User.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
        Optional<User> userFromDb = userRepository.getUserByEmail(user.getEmail());
        if (userFromDb.isPresent()) {
            throw new ConditionsNotRespected(String.format(USER_EXISTS_BY_EMAIL, user.getEmail()));
        }
        User savedUser = userRepository.save(user);

        log.info("ADMIN: Новый пользователь создан: {}", userDto);
        return savedUser;
    }

    @Override
    public List<User> getAllByIds(AdminUserParam adminUserParam) {
        log.info("ADMIN: Запрос на получение всех пользователей по id {}, от {} до {}",
                adminUserParam.getIds(), adminUserParam.getFrom(), adminUserParam.getSize());

        List<Long> ids = adminUserParam.getIds();
        Pageable pageable = new OffsetBasedPageRequest(adminUserParam.getFrom(), adminUserParam.getSize(),
                Sort.by("id").ascending());
        Page<User> users = (ids != null && !ids.isEmpty()) ?
                userRepository.findByIdIn(ids, pageable) : userRepository.findAll(pageable);

        log.info("ADMIN: Получен список пользователей по id: {}", ids);
        return users.getContent();
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        log.info("ADMIN: Получен запрос на удаление пользователя {}", userId);

        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_EXISTING_USER, userId)));
        userRepository.delete(user);

        log.info("ADMIN: Пользователь по id {}", userId);
    }
}
