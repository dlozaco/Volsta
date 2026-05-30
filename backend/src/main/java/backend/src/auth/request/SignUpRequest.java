package backend.src.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {

    @NotBlank
    private String userName;

    private String firstName;

    private String secondName;

    @NotBlank
    private String password;

    @NotBlank
    private String authority;

    private String email;
    private String phoneNumber;
}
