package basket.server.model.input;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import static basket.server.security.validation.validators.ValidationUtil.validate;
import static com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType.MOBILE;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class FormPhoneNumber {

    @NotBlank
    private String regionCode;

    @NotBlank
    private String number;

    public PhoneNumber toValidPhoneNumber(Validator validator) throws ConstraintViolationException {
        validate(this, validator);

        var util = PhoneNumberUtil.getInstance();

        PhoneNumber phoneNumber;
        try {
            phoneNumber = util.parse(regionCode, number);
        } catch (NumberParseException e) {
            throw new ValidationException("Phone number cannot be parsed");
        }

        if (util.isValidNumber(phoneNumber) || util.isPossibleNumberForType(phoneNumber, MOBILE)) {
            throw new ValidationException("Phone number is not a valid mobile number");
        }

        return phoneNumber;
    }
}
