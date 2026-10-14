package com.hoaiphuong.demo.controller;


import com.hoaiphuong.demo.common.exception.ErrorCode;
import com.hoaiphuong.demo.common.pagination.PageQuery;
import com.hoaiphuong.demo.common.pagination.PageableFactory;
import com.hoaiphuong.demo.common.response.ApiResponse;
import com.hoaiphuong.demo.config.openapi.ApiErrorCodes;
import com.hoaiphuong.demo.dto.user.CreateUserRequest;
import com.hoaiphuong.demo.dto.user.UpdateUserRequest;
import com.hoaiphuong.demo.dto.user.UserResponse;
import com.hoaiphuong.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "User", description = "User management")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    // Whitelist: sortBy value sent by the client -> entity field name
    private static final Map<String, String> USER_SORT_MAP = Map.of(
            "name", "name",
            "email", "email");

    private final UserService userService;

    @Operation(summary = "Find a user by name and email",
            description = "Name must match exactly; email is matched case-insensitively.")
    @ApiErrorCodes({ErrorCode.VALIDATION_ERROR, ErrorCode.USER_NOT_FOUND})
    @GetMapping("/lookup")
    public ApiResponse<UserResponse> getUserByNameAndEmail(
            @Parameter(description = "User name (exact match)", example = "hoaiphuong")
            @RequestParam("name") String name,
            @Parameter(description = "User email (case-insensitive)", example = "hoaiphuong@gmail.com")
            @RequestParam("email") String email) {
        return ApiResponse.ok(userService.getUserByNameAndEmail(name, email));
    }

    @Operation(summary = "List users with pagination",
            description = """
                    Lists users using offset pagination, optionally filtered by `name` \
                    (users whose name contains it, case-insensitive).

                    Allowed `sortBy` values: `name` (default), `email`. Results are always also sorted by `id` \
                    ascending to keep a stable order across pages. Page info is returned in `meta` (PageMeta).
                    """)
    @ApiErrorCodes(ErrorCode.VALIDATION_ERROR)
    @GetMapping()
    public ApiResponse<List<UserResponse>> list(
            @Parameter(description = "Text to search for in the name; omit to list all users", example = "phuong")
            @RequestParam(value = "name", defaultValue = "") String name,
            @Valid @ParameterObject PageQuery query) {
        Pageable pageable = PageableFactory.create(query, USER_SORT_MAP, "name");
        return ApiResponse.ok(userService.findAllUserByName(name, pageable));
    }

    @Operation(summary = "Get a user by ID")
    @ApiErrorCodes({ErrorCode.VALIDATION_ERROR, ErrorCode.USER_NOT_FOUND})
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUserById(
            @Parameter(description = "User ID", example = "8be4df61-93ca-11d2-aa0d-00e098032b8c")
            @PathVariable UUID id) {
        return ApiResponse.ok(userService.getUserById(id));
    }

    @Operation(summary = "Create a new user")
    @ApiErrorCodes({ErrorCode.VALIDATION_ERROR, ErrorCode.MALFORMED_REQUEST, ErrorCode.EMAIL_EXISTED})
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid CreateUserRequest request) {
        return ApiResponse.ok(userService.createUser(request));
    }

    @Operation(summary = "Update a user",
            description = "Replaces the user's name and email. The email must not be used by another user.")
    @ApiErrorCodes({ErrorCode.VALIDATION_ERROR, ErrorCode.MALFORMED_REQUEST,
            ErrorCode.USER_NOT_FOUND, ErrorCode.EMAIL_EXISTED})
    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(
            @Parameter(description = "User ID", example = "8be4df61-93ca-11d2-aa0d-00e098032b8c")
            @PathVariable UUID id,
            @RequestBody @Valid UpdateUserRequest request) {
        return ApiResponse.ok(userService.updateUser(id, request));
    }

    @Operation(summary = "Delete a user")
    @ApiErrorCodes({ErrorCode.VALIDATION_ERROR, ErrorCode.USER_NOT_FOUND})
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(
            @Parameter(description = "User ID", example = "8be4df61-93ca-11d2-aa0d-00e098032b8c")
            @PathVariable UUID id) {
        userService.deleteUser(id);
        return ApiResponse.ok();
    }
}
