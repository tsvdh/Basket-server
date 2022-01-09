package basket.server.model.input;

import basket.server.model.DeveloperInfo;
import basket.server.model.User;
import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static basket.server.security.CredentialsValidation.ratePassword;
import static basket.server.security.CredentialsValidation.rateUsername;

@Data
@Component
public class FormUser {

    public enum Type {
        USER,
        DEVELOPER
    }

    private Type userType;

    private String email;
    private String username;
    private String password;

    private String fullName;
    private String phoneNumber;

    private String emailCode;
    private String phoneCode;

    public User toValidUser(PasswordEncoder passwordEncoder, Validator validator) throws ConstraintViolationException {
        if (!rateUsername(password).isEmpty()) {
            throw new ConstraintViolationException("Invalid username", null);
        }
        if (!ratePassword(password).isEmpty()) {
            throw new ConstraintViolationException("Invalid password", null);
        }

        boolean developer = userType.equals(Type.DEVELOPER);

        User user = new User(
                email,
                username,
                passwordEncoder.encode(password),
                new HashSet<>(),
                developer,
                null
        );

        if (developer) {
            DeveloperInfo developerInfo = new DeveloperInfo(
                    fullName,
                    phoneNumber,
                    new HashSet<>(),
                    new HashSet<>()
            );
            Set<ConstraintViolation<DeveloperInfo>> developerViolations = validator.validate(developerInfo);
            if (developerViolations.isEmpty()) {
                user.setDeveloperInfo(developerInfo);
            } else {
                throw new ConstraintViolationException(developerViolations);
            }
        }

        Set<ConstraintViolation<User>> userViolations = validator.validate(user);
        if (userViolations.isEmpty()) {
            return user;
        } else {
            throw new ConstraintViolationException(userViolations);
        }
    }
}
