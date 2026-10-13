package com.hoaiphuong.demo.service;

import com.hoaiphuong.demo.dto.user.CreateUserRequest;
import com.hoaiphuong.demo.dto.user.UserResponse;
import com.hoaiphuong.demo.entity.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserService {
     UserResponse createUser(CreateUserRequest user);
     List<UserEntity> findAll();
     UserResponse getUserByNameAndEmail( String name, String email);

     Page<UserResponse> findAllUserByName(String name, Pageable pageable);
}
