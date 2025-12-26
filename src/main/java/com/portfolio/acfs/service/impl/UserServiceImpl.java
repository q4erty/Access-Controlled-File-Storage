package com.portfolio.acfs.service.impl;

import com.portfolio.acfs.dto.ChangeEmailRequest;
import com.portfolio.acfs.entity.Role;
import com.portfolio.acfs.entity.User;
import com.portfolio.acfs.repository.RoleRepository;
import com.portfolio.acfs.repository.UserRepository;
import com.portfolio.acfs.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getAllUsers() {
        User currentUser = getCurrentUser();
        if (!hasRole(currentUser, Role.RoleType.ADMIN)) {
            throw new RuntimeException("Access denied");
        }
        return userRepository.findAll();
    }

    public User getUserById(Long userId) {
        User currentUser = getCurrentUser();
        if (!hasRole(currentUser, Role.RoleType.ADMIN) && !currentUser.getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public User addRoleToUser(Long userId, Role.RoleType roleType) {
        User currentUser = getCurrentUser();
        if (!hasRole(currentUser, Role.RoleType.ADMIN)) {
            throw new RuntimeException("Only admin can assign roles");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role role = roleRepository.findByName(roleType)
                .orElseGet(() -> roleRepository.save(Role.builder().name(roleType).build()));

        user.getRoles().add(role);
        return userRepository.save(user);
    }

    @Transactional
    public User removeRoleFromUser(Long userId, Role.RoleType roleType) {
        User currentUser = getCurrentUser();
        if (!hasRole(currentUser, Role.RoleType.ADMIN)) {
            throw new RuntimeException("Only admin can remove roles");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.getRoles().removeIf(role -> role.getName() == roleType);
        return userRepository.save(user);
    }

    @Transactional
    public void disableUser(Long userId) {
        User currentUser = getCurrentUser();
        if (!hasRole(currentUser, Role.RoleType.ADMIN)) {
            throw new RuntimeException("Only admin can disable users");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEnabled(false);
        userRepository.save(user);
    }

    @Transactional
    public void enableUser(Long userId) {
        User currentUser = getCurrentUser();
        if (!hasRole(currentUser, Role.RoleType.ADMIN)) {
            throw new RuntimeException("Only admin can enable users");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEnabled(true);
        userRepository.save(user);
    }

    @Transactional
    public void changeEmail(ChangeEmailRequest request) {
        User currentUser = getCurrentUser();

        if (userRepository.existsByEmail(request.getNewEmail())) {
            throw new RuntimeException("Email already in use");
        }

        currentUser.setEmail(request.getNewEmail());
        userRepository.save(currentUser);
    }


    private boolean hasRole(User user, Role.RoleType roleType) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName() == roleType);
    }
}