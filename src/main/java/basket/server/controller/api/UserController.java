package basket.server.controller.api;

import basket.server.service.EmailService;
import basket.server.service.PhoneService;
import basket.server.service.StorageService;
import basket.server.service.UserService;
import basket.server.service.ValidationService;
import basket.server.service.VerificationCodeService;
import basket.server.util.HTMLUtil;
import basket.server.util.HTMLUtil.InputType;
import basket.server.validation.annotations.Email;
import basket.server.validation.validators.EmailValidator;
import basket.server.validation.validators.PasswordValidator;
import basket.server.validation.validators.UsernameValidator;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static basket.server.service.PhoneService.phoneToString;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

@Controller
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {

    private final UserService userService;
    private final VerificationCodeService verificationCodeService;
    private final HTMLUtil htmlUtil;
    private final EmailService emailService;
    private final PhoneService phoneService;
    private final StorageService storageService;
    private final ValidationService validationService;

    @ResponseBody
    @GetMapping(path = "html/valid/username", produces = TEXT_HTML_VALUE)
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

        return htmlUtil.getInputFragment(
                request, response,
                "username",
                username,
                faults,
                InputType.USER
        );
    }

    @ResponseBody
    @GetMapping(path = "html/valid/email", produces = TEXT_HTML_VALUE)
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

        return htmlUtil.getInputFragment(
                request, response,
                "email",
                email,
                faults,
                InputType.USER
        );
    }

    @ResponseBody
    @GetMapping(path = "html/valid/password", produces = TEXT_HTML_VALUE)
    public String getPasswordHTMLResponse(@RequestParam String password, PasswordValidator passwordValidator,
                                          HttpServletRequest request, HttpServletResponse response) {
        List<String> faults;
        if (password.equals("")) {
            faults = null;
        } else {
            faults = passwordValidator.getFaults(password);
        }

        return htmlUtil.getInputFragment(
                request, response,
                "password",
                password,
                faults,
                InputType.USER
        );
    }

    @ResponseBody
    @GetMapping(path = "html/valid/phone", produces = TEXT_HTML_VALUE)
    public String getPhoneHTMLResponse(@RequestParam(name = "formDeveloperInfo.formPhoneNumber.regionCode") String regionCode,
                                       @RequestParam(name = "formDeveloperInfo.formPhoneNumber.number") String number,
                                       HttpServletRequest request, HttpServletResponse response) {
        List<String> faults;
        if (regionCode.equals("") || number.equals("")) {
            faults = null;
        } else {
            try {
                validationService.validateFormPhoneNumber(regionCode, number);
                faults = Collections.emptyList();
            }
            catch (ValidationException e) {
                faults = List.of(e.getMessage());
            }
        }

        return htmlUtil.getInputFragment(
                request, response,
                "phoneNumber",
                number,
                faults,
                InputType.USER
        );
    }

    @ResponseBody
    @GetMapping(path = "html/verify-code/email", produces = TEXT_HTML_VALUE)
    public String getEmailCodeHTMLResponse(@RequestParam String emailCode, @RequestParam @Email String email,
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

        return htmlUtil.getInputFragment(
                request, response,
                "emailCode",
                emailCode,
                faults,
                InputType.USER
        );
    }

    @ResponseBody
    @GetMapping(path = "html/verify-code/phone", produces = TEXT_HTML_VALUE)
    public String getPhoneCodeHTMLResponse(@RequestParam(name = "formDeveloperInfo.formPhoneNumber.regionCode") String regionCode,
                                           @RequestParam(name = "formDeveloperInfo.formPhoneNumber.number") String number,
                                           @RequestParam(name = "formDeveloperInfo.phoneCode") String phoneCode,
                                           HttpServletRequest request, HttpServletResponse response) {
        var phoneNumber = validationService.validateFormPhoneNumber(regionCode, number);
        String formattedNumber = phoneToString(phoneNumber);

        List<String> faults;
        if (phoneCode.equals("")) {
            faults = null;
        } else {
            if (!verificationCodeService.verify(formattedNumber, phoneCode)) {
                faults = List.of("Wrong code");
            } else {
                faults = Collections.emptyList();
            }
        }

        return htmlUtil.getInputFragment(
                request, response,
                "phoneNumberCode",
                phoneCode,
                faults,
                InputType.USER
        );
    }

    @PostMapping("submit/email")
    public ResponseEntity<Void> submitEmail(@RequestParam @NotNull @Email String email) {
        boolean available = userService.getByEmail(email).isEmpty();

        if (!available) {
            return badRequest().build();
        }

        var newCode = verificationCodeService.submit(email);

        emailService.sendVerificationEmail(newCode);

        return ok().build();
    }

    @PostMapping("submit/phone")
    public ResponseEntity<Void> submitPhone(@RequestParam(name = "formDeveloperInfo.formPhoneNumber.regionCode") String regionCode,
                                            @RequestParam(name = "formDeveloperInfo.formPhoneNumber.number") String number) {
        var phoneNumber = validationService.validateFormPhoneNumber(regionCode, number);

        boolean available = userService.getByPhoneNumber(phoneNumber).isEmpty();

        if (!available) {
            return badRequest().build();
        }

        String formattedNumber = phoneToString(phoneNumber);
        var newCode = verificationCodeService.submit(formattedNumber);

        // phoneService.sendVerificationSMS(newCode);
        log.info("Code {} for {}", newCode.getCode(), newCode.getAddress());

        return ok().build();
    }
}
