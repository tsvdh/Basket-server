package basket.server.validation.validators;

import basket.server.validation.FaultChecker;
import basket.server.validation.annotations.Description;
import java.util.List;
import java.util.function.Function;

/**
 * Rules: <br/>
 * - No whitespaces at start or end
 * - At least 20 character, at most 150 characters
 */
public class DescriptionValidator extends FaultChecker<Description, String> {

    @Override
    protected List<Function<String, String>> makeCheckers() {
        return List.of(
                TRIM_CHECKER,

                getLengthChecker(20, 150)
        );
    }
}
