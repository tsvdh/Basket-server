package basket.server.service;

import basket.server.model.App;
import basket.server.model.AppStats;
import basket.server.model.DeveloperInfo;
import basket.server.model.Rating;
import basket.server.model.User;
import basket.server.model.input.FormApp;
import basket.server.model.input.FormDeveloperInfo;
import basket.server.model.input.FormPhoneNumber;
import basket.server.model.input.FormUser;
import basket.server.model.input.FormUser.Type;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static basket.server.service.PhoneService.phoneToString;
import static com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType.MOBILE;

@Service
@RequiredArgsConstructor
public class ValidationService {

    private final Validator validator;
    private final PasswordEncoder passwordEncoder;
    private final VerificationCodeService verificationCodeService;
    private final UserService userService;

    public <T> void validate(T object) throws ConstraintViolationException {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    public App validateFormApp(FormApp formApp, String creatorName) throws ConstraintViolationException {
        validate(formApp);

        Optional<User> optionalUser = userService.getByUsername(creatorName);
        if (optionalUser.isEmpty()) {
            throw new ValidationException("Creator does not exist");
        }

        User creator = optionalUser.get();
        if (!creator.isDeveloper()) {
            throw new ValidationException("Creator must be a developer");
        }

        return new App(
                formApp.getAppName(),
                formApp.getDescription(),
                creator.getId(),
                Set.of(creator.getId()),
                new AppStats(
                        0,
                        new Rating(
                                null,
                                new HashMap<>()
                        )
                ),
                false,
                null,
                null
        );
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
            throw new ValidationException("Cannot be parsed");
        }

        if (!util.isValidNumber(phoneNumber)) {
            throw new ValidationException("Is not a valid number");
        }

        if (!util.isPossibleNumberForType(phoneNumber, MOBILE)) {
            throw new ValidationException("Must be a mobile number");
        }

        return phoneNumber;
    }

    public PhoneNumber validateFormPhoneNumber(String regionCode, String number) throws ConstraintViolationException {
        return validateFormPhoneNumber(new FormPhoneNumber(regionCode, number));
    }
}
