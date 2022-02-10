package basket.server.validation;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.lang.Character.isWhitespace;

public abstract class FaultChecker<A extends Annotation, T> implements ConstraintValidator<A, T> {

    protected static final Function<String, String> WHITESPACE_CHECKER = value -> {
        for (Character c : value.toCharArray()) {
            if (isWhitespace(c)) {
                return "May not contain a whitespace";
            }
        }
        return null;
    };

    protected abstract List<Function<T, String>> makeCheckers();

    public List<String> getFaults(T value) {
        if (value == null) {
            return new LinkedList<>();
        }

        var faults = new HashSet<String>();

        makeCheckers().forEach(checker -> {
            String fault = checker.apply(value);
            if (fault != null) {
                faults.add(fault);
            }
        });

        return new LinkedList<>(faults);
    }

    public boolean isValid(T value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return getFaults(value).isEmpty();
    }
}
