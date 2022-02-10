package basket.server.validation.validators;

import basket.server.validation.FaultChecker;
import basket.server.validation.annotations.Username;
import java.util.List;
import java.util.function.Function;

/**
 * Rules: <br/>
 * - No whitespaces <br/>
 */
public class UsernameValidator extends FaultChecker<Username, String> {

    @Override
    protected List<Function<String, String>> makeCheckers() {
        return List.of(WHITESPACE_CHECKER);
    }
}
