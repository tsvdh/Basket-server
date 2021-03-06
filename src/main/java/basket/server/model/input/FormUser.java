package basket.server.model.input;

import basket.server.util.types.UserType;
import basket.server.validation.annotations.Password;
import basket.server.validation.annotations.Username;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FormUser {

    @NotNull
    private UserType userType;


    @NotNull @Email
    private String email;

    @NotNull @Username
    private String username;

    @NotNull @Password
    private String password;

    @NotBlank
    private String emailCode;

    private FormDeveloperInfo formDeveloperInfo;

    public FormUser() {
        this.formDeveloperInfo = new FormDeveloperInfo();
    }
}
