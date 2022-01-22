package basket.server.api;

import basket.server.messaging.mail.EmailUtil;
import basket.server.model.User;
import basket.server.model.input.FormPhoneNumber;
import basket.server.model.input.FormUser;
import basket.server.security.validation.annotations.Username;
import basket.server.service.UserService;
import basket.server.service.VerificationCodeService;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Validator;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static basket.server.security.validation.validators.ValidationUtil.validate;
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
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;
    private final EmailUtil EMailUtil;

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
        var formPhoneNumber = new FormPhoneNumber(regionCode, number);
        validate(formPhoneNumber, validator);

        var phoneNumber = formPhoneNumber.toValidPhoneNumber(validator);

        return ok(verificationCodeService.get(phoneNumber).isEmpty());
    }

    @GetMapping("api/v1/account/email")
    public ResponseEntity<Void> verifyEmail(@RequestParam @NotBlank @Email String emailAddress) {
        EMailUtil.sendVerificationEmail(emailAddress);
        return ok().build();
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

        //TODO: verify email and phone

        User user = formUser.toValidUser(passwordEncoder, validator);

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
    public ResponseEntity<Void> test(@RequestParam String countryCode, @RequestParam String phoneNumber) throws NumberParseException {
        // log.info("method executed");

        var phone = PhoneNumberUtil.getInstance().parse(phoneNumber, countryCode);
        System.out.println(phone);

        return ok().build();
    }
}
