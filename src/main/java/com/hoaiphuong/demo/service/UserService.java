package com.hoaiphuong.demo.service;

import com.hoaiphuong.demo.dto.user.CreateUserRequest;
import com.hoaiphuong.demo.dto.user.UpdateUserRequest;
import com.hoaiphuong.demo.dto.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;


public interface UserService {
     UserResponse createUser(CreateUserRequest request);
     UserResponse getUserById(UUID id);
     UserResponse getUserByNameAndEmail(String name, String email);

     Page<UserResponse> findAllUserByName(String name, Pageable pageable);

     UserResponse updateUser(UUID id, UpdateUserRequest request);
     void deleteUser(UUID id);
}
