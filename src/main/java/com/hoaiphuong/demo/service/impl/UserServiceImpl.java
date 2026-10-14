package com.hoaiphuong.demo.service.impl;

import com.hoaiphuong.demo.common.exception.BusinessException;
import com.hoaiphuong.demo.common.exception.ErrorCode;
import com.hoaiphuong.demo.dto.user.CreateUserRequest;
import com.hoaiphuong.demo.dto.user.UpdateUserRequest;
import com.hoaiphuong.demo.dto.user.UserResponse;
import com.hoaiphuong.demo.entity.user.UserEntity;
import com.hoaiphuong.demo.mapper.user.UserMapper;
import com.hoaiphuong.demo.repository.UserRepository;
import com.hoaiphuong.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new BusinessException(ErrorCode.EMAIL_EXISTED);
        }
        UserEntity user = userMapper.toUserEntity(request);
        try {
            // Flush now so a concurrent insert of the same email fails here, not at commit time
            return userMapper.toUserResponse(userRepository.saveAndFlush(user));
        } catch (DataIntegrityViolationException ex) {
            throw translateUniqueViolation(ex);
        }
    }

    @Override
    public UserResponse getUserById(UUID id) {
        return userMapper.toUserResponse(findOrThrow(id));
    }

    @Override
    public UserResponse getUserByNameAndEmail(String name, String email) {
        return userRepository.findByNameAndEmailIgnoreCase(name, email)
                .map(userMapper::toUserResponse)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public Page<UserResponse> findAllUserByName(String name, Pageable pageable) {
        Page<UserEntity> page = userRepository.findByNameContainsIgnoreCase(name,pageable);
        return page.map(userMapper::toUserResponse);
    }

    @Override
    @Transactional
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        UserEntity user = findOrThrow(id);
        if (userRepository.existsByEmailIgnoreCaseAndIdNot(request.email(), id)) {
            throw new BusinessException(ErrorCode.EMAIL_EXISTED);
        }
        user.update(request.name(), request.email());
        try {
            userRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw translateUniqueViolation(ex);
        }
        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        userRepository.delete(findOrThrow(id));
    }

    private UserEntity findOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * Email is the only unique column besides the generated id, so a unique violation here means another
     * request saved the same email between our check and the flush. Other violations are rethrown as-is.
     */
    private static RuntimeException translateUniqueViolation(DataIntegrityViolationException ex) {
        for (Throwable cause = ex; cause != null; cause = cause.getCause()) {
            if (cause instanceof ConstraintViolationException cve
                    && cve.getKind() == ConstraintViolationException.ConstraintKind.UNIQUE) {
                return new BusinessException(ErrorCode.EMAIL_EXISTED);
            }
        }
        return ex;
    }
}
