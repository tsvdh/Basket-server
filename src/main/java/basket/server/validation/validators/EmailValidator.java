package basket.server.validation.validators;

import basket.server.validation.FaultChecker;
import basket.server.validation.annotations.Email;
import java.util.List;
import java.util.function.Function;

/**
 * Rules: <br/>
 * - No whitespaces <br/>
 * - Exactly one '@', text on both sides<br/>
 */
public class EmailValidator extends FaultChecker<Email, String> {

    @Override
    protected List<Function<String, String>> makeCheckers() {
        return List.of(
                WHITESPACE_CHECKER,

                value -> {
                    String[] parts = value.split("@");
                    if (parts.length != 2) {
                        return "Must contain one '@' sign, with text on both sides";
                    } else {
                        return null;
                    }
                }
        );
    }
}
