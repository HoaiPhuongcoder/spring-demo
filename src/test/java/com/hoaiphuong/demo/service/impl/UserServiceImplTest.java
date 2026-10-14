package com.hoaiphuong.demo.service.impl;

import com.hoaiphuong.demo.common.exception.BusinessException;
import com.hoaiphuong.demo.common.exception.ErrorCode;
import com.hoaiphuong.demo.dto.user.CreateUserRequest;
import com.hoaiphuong.demo.dto.user.UpdateUserRequest;
import com.hoaiphuong.demo.dto.user.UserResponse;
import com.hoaiphuong.demo.entity.user.UserEntity;
import com.hoaiphuong.demo.mapper.user.UserMapper;
import com.hoaiphuong.demo.repository.UserRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.dao.DataIntegrityViolationException;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    private UserRepository userRepository;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository, Mappers.getMapper(UserMapper.class));
    }

    private static void assertErrorCode(ErrorCode expected, Runnable action) {
        BusinessException ex = assertThrows(BusinessException.class, action::run);
        assertEquals(expected, ex.getErrorCode());
    }

    @Test
    void createUser_savesNewUser() {
        when(userRepository.existsByEmailIgnoreCase("new@mail.com")).thenReturn(false);
        when(userRepository.saveAndFlush(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponse result = userService.createUser(new CreateUserRequest("newuser", "new@mail.com"));

        assertEquals("newuser", result.name());
        assertEquals("new@mail.com", result.email());
    }

    @Test
    void createUser_duplicateEmail_throwsEmailExisted() {
        when(userRepository.existsByEmailIgnoreCase("taken@mail.com")).thenReturn(true);

        assertErrorCode(ErrorCode.EMAIL_EXISTED,
                () -> userService.createUser(new CreateUserRequest("someone", "taken@mail.com")));
        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    void createUser_concurrentDuplicateEmail_throwsEmailExisted() {
        when(userRepository.existsByEmailIgnoreCase("race@mail.com")).thenReturn(false);
        when(userRepository.saveAndFlush(any(UserEntity.class)))
                .thenThrow(constraintViolation(ConstraintViolationException.ConstraintKind.UNIQUE));

        assertErrorCode(ErrorCode.EMAIL_EXISTED,
                () -> userService.createUser(new CreateUserRequest("someone", "race@mail.com")));
    }

    @Test
    void createUser_otherConstraintViolation_isRethrown() {
        when(userRepository.existsByEmailIgnoreCase("new@mail.com")).thenReturn(false);
        DataIntegrityViolationException notNull = constraintViolation(ConstraintViolationException.ConstraintKind.NOT_NULL);
        when(userRepository.saveAndFlush(any(UserEntity.class))).thenThrow(notNull);

        DataIntegrityViolationException thrown = assertThrows(DataIntegrityViolationException.class,
                () -> userService.createUser(new CreateUserRequest("someone", "new@mail.com")));
        assertSame(notNull, thrown);
    }

    @Test
    void updateUser_concurrentDuplicateEmail_throwsEmailExisted() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.of(new UserEntity("oldname", "old@mail.com")));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot("race@mail.com", id)).thenReturn(false);
        doThrow(constraintViolation(ConstraintViolationException.ConstraintKind.UNIQUE)).when(userRepository).flush();

        assertErrorCode(ErrorCode.EMAIL_EXISTED,
                () -> userService.updateUser(id, new UpdateUserRequest("newname", "race@mail.com")));
    }

    @Test
    void requests_normalizeEmail() {
        assertEquals("mixed@mail.com", new CreateUserRequest("someone", "  MiXeD@Mail.COM ").email());
        assertEquals("mixed@mail.com", new UpdateUserRequest("someone", "MIXED@MAIL.COM").email());
    }

    // Same shape Spring produces when the database rejects a statement
    private static DataIntegrityViolationException constraintViolation(ConstraintViolationException.ConstraintKind kind) {
        var cause = new ConstraintViolationException("constraint violated",
                new SQLException("constraint violated"), null, kind, "users_constraint");
        return new DataIntegrityViolationException("could not execute statement", cause);
    }

    @Test
    void getUserById_notFound_throwsUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertErrorCode(ErrorCode.USER_NOT_FOUND, () -> userService.getUserById(id));
    }

    @Test
    void getUserByNameAndEmail_notFound_throwsUserNotFound() {
        when(userRepository.findByNameAndEmailIgnoreCase("ghost", "ghost@mail.com")).thenReturn(Optional.empty());

        assertErrorCode(ErrorCode.USER_NOT_FOUND, () -> userService.getUserByNameAndEmail("ghost", "ghost@mail.com"));
    }

    @Test
    void updateUser_changesNameAndEmail() {
        UUID id = UUID.randomUUID();
        UserEntity user = new UserEntity("oldname", "old@mail.com");
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot("new@mail.com", id)).thenReturn(false);

        UserResponse result = userService.updateUser(id, new UpdateUserRequest("newname", "new@mail.com"));

        assertEquals("newname", result.name());
        assertEquals("new@mail.com", user.getEmail());
    }

    @Test
    void updateUser_emailUsedByAnotherUser_throwsEmailExisted() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.of(new UserEntity("oldname", "old@mail.com")));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot("taken@mail.com", id)).thenReturn(true);

        assertErrorCode(ErrorCode.EMAIL_EXISTED,
                () -> userService.updateUser(id, new UpdateUserRequest("newname", "taken@mail.com")));
    }

    @Test
    void updateUser_notFound_throwsUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertErrorCode(ErrorCode.USER_NOT_FOUND,
                () -> userService.updateUser(id, new UpdateUserRequest("newname", "new@mail.com")));
    }

    @Test
    void deleteUser_deletesExistingUser() {
        UUID id = UUID.randomUUID();
        UserEntity user = new UserEntity("someone", "someone@mail.com");
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.deleteUser(id);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_notFound_throwsUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertErrorCode(ErrorCode.USER_NOT_FOUND, () -> userService.deleteUser(id));
        verify(userRepository, never()).delete(any());
    }
}
