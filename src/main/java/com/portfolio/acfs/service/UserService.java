package com.portfolio.acfs.service;

import com.portfolio.acfs.entity.Role;
import com.portfolio.acfs.entity.User;
import java.util.List;

public interface UserService {

    User getCurrentUser();

    List<User> getAllUsers();

    User getUserById(Long userId);

    User addRoleToUser(Long userId, Role.RoleType roleType);

    User removeRoleFromUser(Long userId, Role.RoleType roleType);

    void disableUser(Long userId);

    void enableUser(Long userId);
}
