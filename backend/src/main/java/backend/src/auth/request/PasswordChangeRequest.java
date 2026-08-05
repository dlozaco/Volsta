package backend.src.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeRequest {

    @NotBlank(message = "Current password can't be null")
    private String currentPassword;

    @NotBlank(message = "New password can't be null")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$",
            message = "Password must be at least 8 characters and include a digit, a lowercase letter, an uppercase letter, and a special character (@#$%^&+=!)."
    )
    private String newPassword;
}
