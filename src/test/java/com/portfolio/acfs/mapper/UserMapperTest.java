package com.portfolio.acfs.mapper;

import com.portfolio.acfs.dto.UserResponse;
import com.portfolio.acfs.entity.Role;
import com.portfolio.acfs.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper userMapper;

    private User user;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);

        user = User.builder()
                .id(1L)
                .email("user@example.com")
                .roles(Set.of(
                        Role.builder().name(Role.RoleType.USER).build(),
                        Role.builder().name(Role.RoleType.ADMIN).build()
                ))
                .build();
    }

    @Test
    void toUserResponse_shouldMapAllFields() {
        UserResponse response = userMapper.toUserResponse(user);

        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getEmail(), response.getEmail());
        assertTrue(response.getRoles().contains("USER"));
        assertTrue(response.getRoles().contains("ADMIN"));
        assertEquals(2, response.getRoles().size());
    }

    @Test
    void toUserResponseList_shouldMapList() {
        List<User> users = List.of(user);
        List<UserResponse> responses = userMapper.toUserResponseList(users);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        UserResponse response = responses.get(0);
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(2, response.getRoles().size());
    }
}
