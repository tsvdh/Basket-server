package basket.server.controller.api;

import basket.server.messaging.email.EmailService;
import basket.server.model.input.FormUser;
import basket.server.service.UserService;
import basket.server.service.VerificationCodeService;
import basket.server.util.HTMLUtil;
import basket.server.validation.annotations.Email;
import basket.server.validation.validators.EmailValidator;
import basket.server.validation.validators.PasswordValidator;
import basket.server.validation.validators.UsernameValidator;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

@Controller
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
@Validated
public class AccountController {

    private final UserService userService;
    private final VerificationCodeService verificationCodeService;
    private final HTMLUtil htmlUtil;
    private final EmailService emailService;


    // @GetMapping("api/v1/account/available/username")
    // public ResponseEntity<Boolean> availableUsername(@RequestParam @NotBlank @Username String username) {
    //     return ok(userService.getByUsername(username).isEmpty());
    // }

    @ResponseBody
    @GetMapping(path = "/html/valid/username", produces = TEXT_HTML_VALUE)
    public String getUsernameHTMLResponse(@RequestParam String username, UsernameValidator validator,
                                          HttpServletRequest request, HttpServletResponse response) {
        List<String> faults;
        if (username.equals("")) {
            faults = null;
        } else {
            faults = validator.getFaults(username);
            if (userService.getByUsername(username).isPresent()) {
                faults.add("Already taken");
            }
        }

        return htmlUtil.getRegisterInputFragment(
                request, response,
                "username",
                username,
                faults
        );
    }

    @ResponseBody
    @GetMapping(path = "/html/valid/email", produces = TEXT_HTML_VALUE)
    public String getEmailHTMLResponse(@RequestParam String email, EmailValidator emailValidator,
                                       HttpServletRequest request, HttpServletResponse response) {
        List<String> faults;
        if (email.equals("")) {
            faults = null;
        } else {
            faults = emailValidator.getFaults(email);
            if (userService.getByEmail(email).isPresent()) {
                faults.add("Already taken");
            }
        }

        return htmlUtil.getRegisterInputFragment(
                request, response,
                "email",
                email,
                faults);
    }

    @ResponseBody
    @GetMapping(path = "/html/valid/password", produces = TEXT_HTML_VALUE)
    public String getPasswordHTMLResponse(@RequestParam String password, PasswordValidator passwordValidator,
                                          HttpServletRequest request, HttpServletResponse response) {
        List<String> faults;
        if (password.equals("")) {
            faults = null;
        } else {
            faults = passwordValidator.getFaults(password);
        }

        return htmlUtil.getRegisterInputFragment(
                request, response,
                "password",
                password,
                faults);
    }

    @ResponseBody
    @GetMapping(path = "/html/verify-code/email", produces = TEXT_HTML_VALUE)
    public String getEmailCodeHTMLResponse(@RequestParam String emailCode, @RequestParam String email,
                                           HttpServletRequest request, HttpServletResponse response) {
        List<String> faults;
        if (emailCode.equals("")) {
            faults = null;
        } else {
            if (!verificationCodeService.verify(email, emailCode)) {
                faults = List.of("Wrong code");
            } else {
                faults = Collections.emptyList();
            }
        }

        return htmlUtil.getRegisterInputFragment(
                request, response,
                "emailCode",
                emailCode,
                faults);
    }

    // @GetMapping("api/v1/account/available/email")
    // public ResponseEntity<Boolean> availableEmail(@RequestParam @NotBlank @Email String emailAddress) {
    //     return ok(verificationCodeService.get(emailAddress).isEmpty());
    // }

    // @GetMapping("api/v1/account/available/phone")
    // public ResponseEntity<Boolean> availablePhoneNumber(@RequestParam @NotBlank String regionCode,
    //                                                     @RequestParam @NotBlank String number) {
    //     PhoneNumber phoneNumber = validationService.validateFormPhoneNumber(regionCode, number);
    //     String formattedNumber = phoneToString(phoneNumber);
    //
    //     return ok(verificationCodeService.get(formattedNumber).isEmpty());
    // }

    @GetMapping("/submit/email")
    public ResponseEntity<Void> submitEmail(@RequestParam @NotNull @Email String email) {
        boolean available = userService.getByEmail(email).isEmpty();

        if (!available) {
            return badRequest().build();
        }

        var newCode = verificationCodeService.submit(email);

        emailService.sendVerificationEmail(newCode);

        return ok().build();
    }

    // @GetMapping("api/v1/account/submit/phone")
    // public ResponseEntity<Void> submitPhone(@RequestParam @NotBlank String regionCode,
    //                                         @RequestParam @NotBlank String number,
    //                                         PhoneService phoneService) {
    //     PhoneNumber phoneNumber = validationService.validateFormPhoneNumber(regionCode, number);
    //     String formattedNumber = phoneToString(phoneNumber);
    //     var verificationCode = generateVerificationCode(formattedNumber);
    //
    //     boolean success = verificationCodeService.submit(verificationCode);
    //
    //     if (!success) {
    //         return badRequest().build();
    //     }
    //
    //     phoneService.sendVerificationSMS(verificationCode);
    //
    //     return ok().build();
    // }

    // @GetMapping("api/v1/account/verify/email")
    // public ResponseEntity<Boolean> verifyEmail(@RequestParam @NotBlank @Email String emailAddress,
    //                                            @RequestParam @NotBlank String code) {
    //     return ok(verificationCodeService.verify(emailAddress, code));
    // }
    //
    // @GetMapping("api/v1/account/verify/phone")
    // public ResponseEntity<Boolean> verifyPhone(@RequestParam @NotBlank String regionCode,
    //                                            @RequestParam @NotBlank String number,
    //                                            @RequestParam @NotBlank String code) {
    //     PhoneNumber phoneNumber = validationService.validateFormPhoneNumber(regionCode, number);
    //     String formattedNumber = phoneToString(phoneNumber);
    //
    //     return ok(verificationCodeService.verify(formattedNumber, code));
    // }



    @GetMapping("X")
    private String intelliSenseHelper(Model model){
        model.addAttribute("formUser", new FormUser());
        return "fragments/input";
    }

    //TODO: forward non-api requests
}
