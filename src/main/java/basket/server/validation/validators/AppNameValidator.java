package basket.server.validation.validators;

import basket.server.validation.FaultChecker;
import basket.server.validation.annotations.AppName;
import java.util.List;
import java.util.function.Function;

import static java.lang.Character.isLetterOrDigit;

/**
 * Rules: <br/>
 * - Numbers and letters only <br/>
 * - No whitespaces at start or end <br/>
 * - At least 3 characters, at most 50 characters
 */
public class AppNameValidator extends FaultChecker<AppName, String> {

    @Override
    protected List<Function<String, String>> makeCheckers() {
        return List.of(
                value -> {
                    for (Character c : value.toCharArray()) {
                        if (!isLetterOrDigit(c)) {
                            return "May only contain numbers and letters";
                        }
                    }
                    return null;
                },

                TRIM_CHECKER,

                getLengthChecker(3, 50)
        );
    }
}
