package com.hoaiphuong.demo.dto.user;

import com.hoaiphuong.demo.common.util.EmailUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@Schema(description = "Data for updating a user (full replacement: every field is required)")
public record UpdateUserRequest(

        @Schema(description = "User name", example = "hoaiphuong",
                minLength = 6, maxLength = 50, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Length(min = 6, max = 50)
        String name,

        @Schema(description = "Email, must be well-formed and not used by another user. Stored trimmed and lower-cased", example = "hoaiphuong@gmail.com",
                format = "email", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Email
        String email
) {
    public UpdateUserRequest {
        email = EmailUtils.normalize(email);
    }
}
