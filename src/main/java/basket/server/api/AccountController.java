package basket.server.api;

import basket.server.messaging.mail.EmailService;
import basket.server.messaging.phone.PhoneService;
import basket.server.model.User;
import basket.server.model.input.FormUser;
import basket.server.security.validation.ValidationService;
import basket.server.security.validation.annotations.Username;
import basket.server.service.UserService;
import basket.server.service.VerificationCodeService;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static basket.server.messaging.phone.PhoneService.phoneToString;
import static basket.server.model.VerificationCode.generateVerificationCode;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AccountController {

    private final UserService userService;
    private final VerificationCodeService verificationCodeService;
    private final ValidationService validationService;
    private final EmailService emailService;
    private final PhoneService phoneService;

    @GetMapping("api/v1/account/available/username")
    public ResponseEntity<Boolean> availableUsername(@RequestParam @NotBlank @Username String username) {
        return ok(userService.getByUsername(username).isEmpty());
    }

    @GetMapping("api/v1/account/available/email")
    public ResponseEntity<Boolean> availableEmail(@RequestParam @NotBlank @Email String email) {
        return ok(verificationCodeService.get(email).isEmpty());
    }

    @GetMapping("api/v1/account/available/phone")
    public ResponseEntity<Boolean> availablePhoneNumber(@RequestParam @NotBlank String regionCode,
                                                        @RequestParam @NotBlank String number) {
        PhoneNumber phoneNumber = validationService.validateFormPhoneNumber(regionCode, number);
        String formattedNumber = phoneToString(phoneNumber);

        return ok(verificationCodeService.get(formattedNumber).isEmpty());
    }

    @GetMapping("api/v1/account/submit/email")
    public ResponseEntity<Void> submitEmail(@RequestParam @NotBlank @Email String emailAddress) {
        var verificationCode = generateVerificationCode(emailAddress);

        boolean success = verificationCodeService.submit(verificationCode);

        if (!success) {
            return badRequest().build();
        }

        emailService.sendVerificationEmail(verificationCode);

        return ok().build();
    }

    @GetMapping("api/v1/account/submit/phone")
    public ResponseEntity<Void> submitPhone(@RequestParam @NotBlank String regionCode,
                                            @RequestParam @NotBlank String number) {
        PhoneNumber phoneNumber = validationService.validateFormPhoneNumber(regionCode, number);
        String formattedNumber = phoneToString(phoneNumber);
        var verificationCode = generateVerificationCode(formattedNumber);

        boolean success = verificationCodeService.submit(verificationCode);

        if (!success) {
            return badRequest().build();
        }

        phoneService.sendVerificationSMS(verificationCode);

        return ok().build();
    }

    @GetMapping("api/v1/account/verify/email")
    public ResponseEntity<Boolean> verifyEmail(@RequestParam @NotBlank @Email String emailAddress,
                                               @RequestParam @NotBlank String code) {
        return ok(verificationCodeService.verify(emailAddress, code));
    }

    @GetMapping("api/v1/account/verify/phone")
    public ResponseEntity<Boolean> verifyPhone(@RequestParam @NotBlank String regionCode,
                                               @RequestParam @NotBlank String number,
                                               @RequestParam @NotBlank String code) {
        PhoneNumber phoneNumber = validationService.validateFormPhoneNumber(regionCode, number);
        String formattedNumber = phoneToString(phoneNumber);

        return ok(verificationCodeService.verify(formattedNumber, code));
    }

    @GetMapping("login")
    public String getLogin() {
        return "login";
    }

    @GetMapping("register")
    public String getRegistration(Model model) {
        model.addAttribute("formUser", new FormUser());
        return "register";
    }

    //TODO: forward non-api requests

    @PostMapping("register")
    public ResponseEntity<Void> register(@ModelAttribute FormUser formUser,
                                         HttpServletResponse response) throws IOException {
        User user = validationService.validateFormUser(formUser);

        boolean success = userService.add(user);

        if (success) {
            if (user.isDeveloper()) {
                response.sendRedirect("/developers/" + user.getUsername());
            } else {
                response.sendRedirect("/");
            }
            return ok().build();
        } else {
            return badRequest().build();
        }
    }

    @PostMapping("register/developer/existing")
    public ResponseEntity<Void> register(HttpServletRequest request,
                                         @RequestParam @NotBlank String fullName,
                                         @RequestParam @NotBlank String phoneNumber) {
        String userName = request.getRemoteUser();
        if (userName == null) {
            return badRequest().build();
        }

        //TODO: upgrade user

        return ok().build();
    }

    @GetMapping("api/v1/test")
    public ResponseEntity<Void> test(HttpServletRequest request) throws NumberParseException {
        log.info("method executed");



        return ok().build();
    }
}
