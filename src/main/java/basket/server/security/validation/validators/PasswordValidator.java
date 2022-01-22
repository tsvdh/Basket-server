package basket.server.security.validation.validators;

import basket.server.security.validation.annotations.Password;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.lang.Character.isDigit;
import static java.lang.Character.isLetter;
import static java.lang.Character.isWhitespace;

/**
 * Rules:
 * - At least 8 characters
 * - A number
 * - A letter
 * - No whitespaces
 */
public class PasswordValidator implements ConstraintValidator<Password, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (value.length() < 8) {
            return false;
        }

        boolean number = false;
        boolean letter = false;

        for (Character c : value.toCharArray()) {
            if (isWhitespace(c)) {
                return false;
            }
            if (isDigit(c)) {
                number = true;
            } else if (isLetter(c)) {
                letter = true;
            }
        }

        return number && letter;
    }
}
