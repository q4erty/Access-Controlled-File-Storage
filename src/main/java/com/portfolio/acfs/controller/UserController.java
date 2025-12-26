package com.portfolio.acfs.controller;

import com.portfolio.acfs.dto.AddRoleRequest;
import com.portfolio.acfs.dto.ChangeEmailRequest;
import com.portfolio.acfs.dto.MessageResponse;
import com.portfolio.acfs.dto.UserResponse;
import com.portfolio.acfs.entity.Role;
import com.portfolio.acfs.entity.User;
import com.portfolio.acfs.mapper.UserMapper;
import com.portfolio.acfs.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;
    private final UserMapper userMapper;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        User user = userService.getCurrentUser();
        return ResponseEntity.ok(userMapper.toUserResponse(user));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(userMapper.toUserResponseList(users));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(userMapper.toUserResponse(user));
    }

    @PostMapping("/{userId}/roles")
    public ResponseEntity<UserResponse> addRole(
            @PathVariable Long userId,
            @Valid @RequestBody AddRoleRequest request) {
        Role.RoleType roleType = Role.RoleType.valueOf(request.getRole());
        User user = userService.addRoleToUser(userId, roleType);
        return ResponseEntity.ok(userMapper.toUserResponse(user));
    }

    @DeleteMapping("/{userId}/roles/{role}")
    public ResponseEntity<UserResponse> removeRole(
            @PathVariable Long userId,
            @PathVariable String role) {
        Role.RoleType roleType = Role.RoleType.valueOf(role);
        User user = userService.removeRoleFromUser(userId, roleType);
        return ResponseEntity.ok(userMapper.toUserResponse(user));
    }

    @PutMapping("/{userId}/disable")
    public ResponseEntity<MessageResponse> disableUser(@PathVariable Long userId) {
        userService.disableUser(userId);
        return ResponseEntity.ok(new MessageResponse("User disabled successfully"));
    }

    @PutMapping("/{userId}/enable")
    public ResponseEntity<MessageResponse> enableUser(@PathVariable Long userId) {
        userService.enableUser(userId);
        return ResponseEntity.ok(new MessageResponse("User enabled successfully"));
    }

    @PatchMapping("/email")
    public ResponseEntity<Void> changeEmail(
            @Valid @RequestBody ChangeEmailRequest request
    ) {
        userService.changeEmail(request);
        return ResponseEntity.noContent().build();
    }
}