package com.fooddelivery.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data Transfer Object for password update requests.
 * Contains current password for verification and
 * new password with confirmation to ensure correctness.
 * Used by {@code AuthController} for the update-password endpoint.
 */
@Data
public class UpdatePasswordRequest {

    /**
     * Current password of the user in plain text.
     * Verified against the BCrypt hashed password in auth_db
     * before allowing the update.
     * Cannot be blank.
     */
    @NotBlank(message = "Current password is required")
    private String currentPassword;

    /**
     * New password to replace the current password.
     * Must be different from the current password.
     * Will be BCrypt encoded before storing in auth_db.
     * Cannot be blank.
     */
    @NotBlank(message = "New password is required")
    private String newPassword;

    /**
     * Confirmation of the new password.
     * Must exactly match {@code newPassword} to prevent typos.
     * Cannot be blank.
     */
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}