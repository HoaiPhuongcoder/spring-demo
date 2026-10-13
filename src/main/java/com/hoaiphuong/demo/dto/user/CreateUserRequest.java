package com.hoaiphuong.demo.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.Length;

public record CreateUserRequest(

        @Schema(description = "name", example = "hoaiphuong")
        @Length(min = 6, max = 50)
        String name,

        @Schema(description = "email", example = "hoaiphuong@gmail.com")
        @Email
        String email
) {}
