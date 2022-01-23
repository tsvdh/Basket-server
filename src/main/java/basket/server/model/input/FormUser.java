package basket.server.model.input;

import basket.server.security.validation.annotations.Password;
import basket.server.security.validation.annotations.Username;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FormUser {

    public enum Type {
        USER,
        DEVELOPER
    }

    @NotNull
    private Type userType;


    @NotNull @Email
    private String email;

    @NotNull @Username
    private String username;

    @NotNull @Password
    private String password;

    @NotBlank
    private String emailCode;

    private FormDeveloperInfo formDeveloperInfo;
}
