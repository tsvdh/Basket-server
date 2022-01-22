package basket.server.model.input;

import basket.server.model.User;
import basket.server.security.validation.annotations.Password;
import basket.server.security.validation.annotations.Username;
import java.util.HashSet;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

import static basket.server.security.validation.validators.ValidationUtil.validate;

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

    // @NotBlank
    private String emailCode;

    private FormDeveloperInfo formDeveloperInfo;

    public User toValidUser(PasswordEncoder passwordEncoder, Validator validator) throws ConstraintViolationException {
        validate(this, validator);

        boolean developer = userType.equals(Type.DEVELOPER);

        var user = new User(
                email,
                username,
                passwordEncoder.encode(password),
                new HashSet<>(),
                developer,
                null
        );

        if (developer) {
            user.setDeveloperInfo(formDeveloperInfo.toValidDeveloperInfo(validator));
        }

        return user;
    }
}
