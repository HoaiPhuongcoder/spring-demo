package com.hoaiphuong.demo.service.impl;

import com.hoaiphuong.demo.dto.user.CreateUserRequest;
import com.hoaiphuong.demo.dto.user.UserResponse;
import com.hoaiphuong.demo.entity.user.UserEntity;
import com.hoaiphuong.demo.mapper.user.UserMapper;
import com.hoaiphuong.demo.repository.UserRepository;
import com.hoaiphuong.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse createUser(CreateUserRequest userRequest) {
        UserEntity user = userMapper.toUserEntity(userRequest);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    @Override
    public UserResponse getUserByNameAndEmail(String name, String email) {
        return userMapper.toUserResponse(userRepository.findByNameAndEmail(name, email));
    }

    @Override
    public Page<UserResponse> findAllUserByName(String name, Pageable pageable) {
        Page<UserEntity> page = userRepository.findByNameContainsIgnoreCase(name,pageable);
        return page.map(userMapper::toUserResponse);
    }
}
