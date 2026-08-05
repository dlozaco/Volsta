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
            message = "Password must have 8 characters at least, a minus, mayus and a special character (@#$%^&+=!)."
    )
    private String newPassword;
}
