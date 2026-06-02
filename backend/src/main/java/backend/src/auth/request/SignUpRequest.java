package backend.src.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {

    @NotBlank(
            message = "Username can't be null"
    )
    private String userName;

    private String firstName;

    private String secondName;

    @NotBlank(
            message = "Password can't be null"
    )
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$",
            message = "Password must have 8 characters at least, a minus, mayus and a special character (@#$%^&+=!)."
    )
    private String password;

    @NotBlank(
            message = "Authority can't be null"
    )
    private String authority;

    private String email;
    private String phoneNumber;
}
