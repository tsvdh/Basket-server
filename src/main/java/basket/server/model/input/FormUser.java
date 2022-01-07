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

@Data
@Component
public class FormUser {

    public enum Type {
        USER,
        DEVELOPER
    }

    private final PasswordEncoder passwordEncoder;
    private final Validator validator;

    private Type userType;

    private String email;
    private String username;
    private String password;

    private String fullName;
    private String phoneNumber;

    private String emailCode;
    private String phoneCode;

    public User toValidUser() throws ConstraintViolationException {
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

        //TODO: validate username and password

        Set<ConstraintViolation<User>> userViolations = validator.validate(user);
        if (userViolations.isEmpty()) {
            return user;
        } else {
            throw new ConstraintViolationException(userViolations);
        }
    }
}
