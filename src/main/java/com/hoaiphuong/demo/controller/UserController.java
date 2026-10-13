package com.hoaiphuong.demo.controller;


import com.hoaiphuong.demo.common.response.ApiResponse;
import com.hoaiphuong.demo.dto.user.CreateUserRequest;
import com.hoaiphuong.demo.dto.user.UserResponse;
import com.hoaiphuong.demo.entity.user.UserEntity;
import com.hoaiphuong.demo.mapper.user.UserMapper;
import com.hoaiphuong.demo.repository.UserRepository;
import com.hoaiphuong.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    public List<UserEntity> findAll() {
        return userService.findAll();
    }

    @GetMapping()
    public ResponseEntity<UserResponse> getUserByNameAndEmail(@RequestParam("name") String name, @RequestParam("email") String email) {
        return ResponseEntity.ok(userService.getUserByNameAndEmail(name, email));
    }

    @GetMapping("/search")
    public ApiResponse<List<UserResponse>> search(
            @RequestParam(value = "name", defaultValue = "") String name,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable ) {
        return ApiResponse.ok(userService.findAllUserByName(name, pageable));
    }

    @PostMapping()
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid CreateUserRequest user) {

        return ResponseEntity.ok(userService.createUser(user));
    }
}
