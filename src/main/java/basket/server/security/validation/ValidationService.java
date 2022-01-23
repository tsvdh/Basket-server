package basket.server.security.validation;

import basket.server.model.DeveloperInfo;
import basket.server.model.User;
import basket.server.model.input.FormDeveloperInfo;
import basket.server.model.input.FormPhoneNumber;
import basket.server.model.input.FormUser;
import basket.server.model.input.FormUser.Type;
import basket.server.service.VerificationCodeService;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static basket.server.messaging.phone.PhoneService.phoneToString;
import static com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType.MOBILE;

@Service
@RequiredArgsConstructor
public class ValidationService {

    private final Validator validator;
    private final PasswordEncoder passwordEncoder;
    private final VerificationCodeService verificationCodeService;

    public <T> void validate(T object) throws ConstraintViolationException {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    public User validateFormUser(FormUser formUser) throws ConstraintViolationException {
        validate(formUser);

        boolean emailVerified = verificationCodeService.verify(formUser.getEmail(), formUser.getEmailCode());
        if (!emailVerified) {
            throw new ValidationException("Email is not verified");
        }

        boolean developer = formUser.getUserType().equals(Type.DEVELOPER);

        var user = new User(
                formUser.getEmail(),
                formUser.getUsername(),
                passwordEncoder.encode(formUser.getPassword()),
                new HashSet<>(),
                developer,
                null
        );

        if (developer) {
            var developerInfo = validateFormDeveloperInfo(formUser.getFormDeveloperInfo());
            user.setDeveloperInfo(developerInfo);
        }

        return user;
    }

    public DeveloperInfo validateFormDeveloperInfo(FormDeveloperInfo formDeveloperInfo) throws ConstraintViolationException {
        validate(formDeveloperInfo);

        var phoneNumber = validateFormPhoneNumber(formDeveloperInfo.getFormPhoneNumber());

        boolean phoneVerified = verificationCodeService.verify(
                phoneToString(phoneNumber),
                formDeveloperInfo.getPhoneCode()
        );

        if (!phoneVerified) {
            throw new ValidationException("Phone number is not verified");
        }

        return new DeveloperInfo(
                formDeveloperInfo.getFirstName(),
                formDeveloperInfo.getLastName(),
                phoneNumber,
                new HashSet<>(),
                new HashSet<>()
        );
    }

    public PhoneNumber validateFormPhoneNumber(FormPhoneNumber formPhoneNumber) throws ConstraintViolationException {
        validate(formPhoneNumber);

        var util = PhoneNumberUtil.getInstance();

        PhoneNumber phoneNumber;
        try {
            phoneNumber = util.parse(formPhoneNumber.getNumber(), formPhoneNumber.getRegionCode());
        } catch (NumberParseException e) {
            throw new ValidationException("Phone number cannot be parsed");
        }

        if (!util.isValidNumber(phoneNumber) || !util.isPossibleNumberForType(phoneNumber, MOBILE)) {
            throw new ValidationException("Phone number is not a valid mobile number");
        }

        return phoneNumber;
    }

    public PhoneNumber validateFormPhoneNumber(String regionCode, String number) throws ConstraintViolationException {
        return validateFormPhoneNumber(new FormPhoneNumber(regionCode, number));
    }
}
