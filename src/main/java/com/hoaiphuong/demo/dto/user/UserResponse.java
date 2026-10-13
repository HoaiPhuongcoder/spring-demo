package com.hoaiphuong.demo.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record UserResponse(
        @Schema(description = "id of user", example = "8be4df61-93ca-11d2-aa0d-00e098032b8c")
        UUID id,

        @Schema(description = "name of user", example = "locgay")
        String name,

        @Schema(description = "email of user", example = "locgay@gmail.com")
        String email
) {}
