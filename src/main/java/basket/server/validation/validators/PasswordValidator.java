package basket.server.validation.validators;

import basket.server.validation.FaultChecker;
import basket.server.validation.annotations.Password;
import java.util.List;
import java.util.function.Function;

import static java.lang.Character.isDigit;
import static java.lang.Character.isLetter;

/**
 * Rules: <br/>
 * - No whitespaces <br/>
 * - At least 8 characters <br/>
 * - A number <br/>
 * - A letter <br/>
 */
public class PasswordValidator extends FaultChecker<Password, String> {

    @Override
    protected List<Function<String, String>> makeCheckers() {
        return List.of(
                WHITESPACE_CHECKER,

                getLengthChecker(8, Integer.MAX_VALUE),

                value -> {
                    for (Character c : value.toCharArray()) {
                        if (isDigit(c)) {
                            return null;
                        }
                    }
                    return "Must contain a number";
                },

                value -> {
                    for (Character c : value.toCharArray()) {
                        if (isLetter(c)) {
                            return null;
                        }
                    }
                    return "Must contain a letter";
                }
        );
    }
}
