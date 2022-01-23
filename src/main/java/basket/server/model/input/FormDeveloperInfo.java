package basket.server.model.input;

import basket.server.model.DeveloperInfo;
import java.util.HashSet;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import static basket.server.security.validation.ValidationUtil.validate;

@Data
@Slf4j
public class FormDeveloperInfo {

    @NotNull
    private FormPhoneNumber formPhoneNumber;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    // @NotBlank
    private String phoneCode;

    public DeveloperInfo toValidDeveloperInfo(Validator validator) throws ConstraintViolationException {
        validate(this, validator);

        return new DeveloperInfo(
                firstName + " " + lastName,
                formPhoneNumber.toValidPhoneNumber(validator),
                new HashSet<>(),
                new HashSet<>()
        );
    }
}
