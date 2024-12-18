package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.AdminUserParam;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.dto.CreateUserDto;

import java.util.List;

public interface UserService {

    User create(CreateUserDto userDto);

    List<User> getAllByIds(AdminUserParam adminUserParam);

    void delete(Long userId);
}
