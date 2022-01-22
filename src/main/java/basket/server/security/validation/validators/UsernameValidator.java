package basket.server.security.validation.validators;

import basket.server.security.validation.annotations.Username;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.lang.Character.isWhitespace;

/**
 * Rules:
 * - No whitespaces
 */
public class UsernameValidator implements ConstraintValidator<Username, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        for (Character c : value.toCharArray()) {
            if (isWhitespace(c)) {
                return false;
            }
        }

        return true;
    }
}
