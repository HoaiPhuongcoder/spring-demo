package com.hoaiphuong.demo.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "User information")
public record UserResponse(
        @Schema(description = "User ID", example = "8be4df61-93ca-11d2-aa0d-00e098032b8c")
        UUID id,

        @Schema(description = "User name", example = "hoaiphuong")
        String name,

        @Schema(description = "User email", example = "hoaiphuong@gmail.com")
        String email
) {}
