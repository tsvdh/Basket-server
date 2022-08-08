package basket.server.service;

import basket.server.model.app.App;
import basket.server.model.app.AppStats;
import basket.server.model.app.Rating;
import basket.server.model.expiring.PendingUpload;
import basket.server.model.input.FormApp;
import basket.server.model.input.FormDeveloperInfo;
import basket.server.model.input.FormPendingUpload;
import basket.server.model.input.FormPhoneNumber;
import basket.server.model.input.FormUser;
import basket.server.model.user.DeveloperInfo;
import basket.server.model.user.User;
import basket.server.util.types.UserType;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import java.util.HashMap;
import java.util.HashSet;
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

    public <T> void validateConstraints(T object) throws ConstraintViolationException {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    public App validate(FormApp formApp, User creator) throws ConstraintViolationException {
        return validate(formApp, null, creator);
    }

    public App validate(FormApp formApp, App oldApp) throws ConstraintViolationException {
        return validate(formApp, oldApp, null);
    }

    private App validate(FormApp formApp, App oldApp, User creator) {
        validateConstraints(formApp);

        if (oldApp == null && creator == null ) {
            throw new ValidationException("App and creator are both null");
        }

        if (creator != null && !creator.isDeveloper()) {
            throw new ValidationException("Creator must be a developer");
        }

        return new App(
                formApp.getAppName(),
                formApp.getDescription(),
                oldApp != null ? oldApp.getAdmin() : creator.getId(),
                oldApp != null ? oldApp.getDevelopers() : new HashSet<>(Set.of(creator.getId())),
                oldApp != null ? oldApp.getAppStats()
                        : new AppStats(
                        0,
                        new Rating(
                                null,
                                new HashMap<>()
                        )

                ),
                oldApp != null && oldApp.isAvailable(),
                oldApp != null ? oldApp.getStable() : null,
                oldApp != null ? oldApp.getExperimental() : null
        );
    }

    public User validate(FormUser formUser) throws ConstraintViolationException {
        return validate(formUser, null);
    }

    public User validate(FormUser formUser, User oldUser) throws ConstraintViolationException {
        validateConstraints(formUser);

        boolean emailVerified = verificationCodeService.verify(formUser.getEmail(), formUser.getEmailCode());
        if (!emailVerified) {
            throw new ValidationException("Email is not verified");
        }

        boolean developer = formUser.getUserType().equals(UserType.DEVELOPER);

        var user = new User(
                formUser.getEmail(),
                formUser.getUsername(),
                passwordEncoder.encode(formUser.getPassword()),
                oldUser != null ? oldUser.getUsageInfo() : new HashMap<>(),
                developer,
                null
        );

        DeveloperInfo oldInfo = null;
        if (oldUser != null && oldUser.isDeveloper()) {
            oldInfo = oldUser.getDeveloperInfo();
        }

        if (developer) {
            var developerInfo = validate(formUser.getFormDeveloperInfo(), oldInfo);
            user.setDeveloperInfo(developerInfo);
        }

        return user;
    }

    public DeveloperInfo validate(FormDeveloperInfo formDeveloperInfo) throws ConstraintViolationException {
        return validate(formDeveloperInfo, null);
    }

    public DeveloperInfo validate(FormDeveloperInfo formDeveloperInfo, DeveloperInfo oldInfo) throws ConstraintViolationException {
        validateConstraints(formDeveloperInfo);

        var phoneNumber = validate(formDeveloperInfo.getFormPhoneNumber());

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
                oldInfo != null ? oldInfo.getDeveloperOf() : new HashSet<>(),
                oldInfo != null ? oldInfo.getAdminOf() : new HashSet<>()
        );
    }

    public PhoneNumber validate(FormPhoneNumber formPhoneNumber) throws ConstraintViolationException {
        validateConstraints(formPhoneNumber);

        var util = PhoneNumberUtil.getInstance();

        PhoneNumber phoneNumber;
        try {
            phoneNumber = util.parse(formPhoneNumber.getNumber(), formPhoneNumber.getRegionCode());
        } catch (NumberParseException e) {
            throw new ValidationException("Cannot be parsed");
        }

        if (!util.isValidNumber(phoneNumber)) {
            throw new ValidationException("Not a valid number");
        }

        if (!util.isPossibleNumberForType(phoneNumber, MOBILE)) {
            throw new ValidationException("Must be a mobile number");
        }

        return phoneNumber;
    }

    public PhoneNumber validate(String regionCode, String number) throws ConstraintViolationException {
        return validate(new FormPhoneNumber(regionCode, number));
    }

    public PendingUpload validate(FormPendingUpload formPendingUpload) throws ConstraintViolationException {
        validateConstraints(formPendingUpload);

        String type = formPendingUpload.getType();

        switch (type) {
            case "icon":
            case "stable":
            case "experimental":
                break;
            default:
                throw new ValidationException("Upload type does not exist");
        }

        return new PendingUpload(
                formPendingUpload.getAppId(),
                type,
                formPendingUpload.getVersion()
        );
    }
}
