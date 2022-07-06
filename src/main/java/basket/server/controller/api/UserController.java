package basket.server.controller.api;

import basket.server.model.expiring.VerificationCode;
import basket.server.model.input.FormDeveloperInfo;
import basket.server.model.input.SecureFormUser;
import basket.server.model.user.User;
import basket.server.service.EmailService;
import basket.server.service.PhoneService;
import basket.server.service.StorageService;
import basket.server.service.UserService;
import basket.server.service.ValidationService;
import basket.server.service.VerificationCodeService;
import basket.server.util.ControllerUtil;
import basket.server.util.HTMLUtil;
import basket.server.util.HTMLUtil.InputType;
import basket.server.util.IllegalActionException;
import basket.server.util.types.UserType;
import basket.server.validation.annotations.Email;
import basket.server.validation.validators.EmailValidator;
import basket.server.validation.validators.PasswordValidator;
import basket.server.validation.validators.UsernameValidator;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;

import static basket.server.service.PhoneService.phoneToString;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.internalServerError;
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
    private final AuthenticationManager authManager;
    private final ControllerUtil controllerUtil;

    private void checkTaken(Optional<User> takenBy, HttpServletRequest request, List<String> faults) {
        if (takenBy.isEmpty()) {
            return;
        }

        if (request.getRemoteUser() == null) {
            return;
        }

        if (request.getRemoteUser().equals(takenBy.get().getUsername())) {
            return;
        }

        faults.add("Already taken");
    }

    @ResponseBody
    @GetMapping(path = "html/valid/username", produces = TEXT_HTML_VALUE)
    public String getUsernameHTMLResponse(@RequestParam String username, UsernameValidator validator,
                                          HttpServletRequest request, HttpServletResponse response) {
        List<String> faults;
        if (username.equals("")) {
            faults = null;
        } else {
            faults = validator.getFaults(username);
            checkTaken(userService.getByUsername(username), request, faults);
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
            checkTaken(userService.getByEmail(email), request, faults);
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
                PhoneNumber phoneNumber = validationService.validate(regionCode, number);
                faults = Collections.emptyList();
                checkTaken(userService.getByPhoneNumber(phoneNumber), request, faults);
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
    public String getEmailCodeHTMLResponse(@RequestParam String emailCode, @RequestParam String email,
                                           EmailValidator emailValidator,
                                           HttpServletRequest request, HttpServletResponse response) {
        List<String> faults;
        if (emailCode.equals("") || !emailValidator.getFaults(email).isEmpty()) {
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
        String formattedNumber;
        try {
            var phoneNumber = validationService.validate(regionCode, number);
            formattedNumber = phoneToString(phoneNumber);
        } catch (ValidationException e) {
            formattedNumber = null;
        }

        List<String> faults;
        if (phoneCode.equals("") || formattedNumber == null) {
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

        // emailService.sendVerificationEmail(newCode);
        log.info("Code {} for {}", newCode.getCode(), newCode.getAddress());

        return ok().build();
    }

    @PostMapping("submit/phone")
    public ResponseEntity<Void> submitPhone(@RequestParam(name = "formDeveloperInfo.formPhoneNumber.regionCode") String regionCode,
                                            @RequestParam(name = "formDeveloperInfo.formPhoneNumber.number") String number) {
        var phoneNumber = validationService.validate(regionCode, number);

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

    @GetMapping("html/settings/verify")
    @PreAuthorize("hasRole('USER')")
    public String verifyPassword(@RequestParam String currentPassword, Principal principal, Model model) {
        try {
            Authentication auth = new UsernamePasswordAuthenticationToken(principal.getName(), currentPassword);
            authManager.authenticate(auth);
        }
        catch (BadCredentialsException e) {
            return "fragments/elements/user/settings :: wrong-password";
        }

        User pageUser = controllerUtil.getUser(principal.getName());

        VerificationCode emailCode = verificationCodeService.submit(pageUser.getEmail());

        VerificationCode phoneCode;
        if (pageUser.isDeveloper()) {
            String formattedNumber = phoneToString(pageUser.getDeveloperInfo().getPhoneNumber());
            phoneCode = verificationCodeService.submit(formattedNumber);
        } else {
            phoneCode = null;
        }

        model.addAttribute("pageUser", pageUser);
        model.addAttribute("formUser", new SecureFormUser());
        model.addAttribute("countryCodeList", HTMLUtil.getCountryList());
        model.addAttribute("emailCode", emailCode);
        model.addAttribute("phoneCode", phoneCode);
        model.addAttribute("phoneNumberUtil", PhoneNumberUtil.getInstance());
        model.addAttribute("currentPassword", currentPassword);

        return "fragments/elements/user/settings :: correct-password";
    }

    @PostMapping("info/change")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> changeInfo(@ModelAttribute SecureFormUser formUser, Principal principal,
                                           HttpServletResponse response) throws IOException {

        var optionalUser = userService.getByUsername(principal.getName());
        if (optionalUser.isEmpty()) {
            // should not be possible as user is authenticated
            return internalServerError().build();
        }

        var user = optionalUser.get();

        UserType userType;

        if (user.isDeveloper()) {
            userType = UserType.DEVELOPER;
        } else {
            userType = UserType.USER;
        }

        if (!userType.equals(formUser.getUserType())) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "New type of user is not equal to existing type");
        }

        try {
            Authentication oldAuth = new UsernamePasswordAuthenticationToken(principal.getName(), formUser.getCurrentPassword());
            authManager.authenticate(oldAuth);

            userService.update(formUser, user);
        }
        catch (BadCredentialsException | IllegalActionException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        Authentication newAuth = new UsernamePasswordAuthenticationToken(formUser.getUsername(), formUser.getPassword());

        SecurityContextHolder.getContext().setAuthentication(newAuth);

        response.sendRedirect("/users/%s/settings".formatted(formUser.getUsername()));

        return ok().build();
    }

    @PostMapping("info/new/developer")
    public ResponseEntity<Void> newDeveloper(@ModelAttribute FormDeveloperInfo formDeveloperInfo, Authentication auth,
                                             HttpServletResponse response) throws IOException {



        List<GrantedAuthority> authorities = new ArrayList<>(auth.getAuthorities());
        authorities.add(new SimpleGrantedAuthority("ROLE_DEVELOPER"));

        Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), authorities);

        SecurityContextHolder.getContext().setAuthentication(newAuth);

        response.sendRedirect("/users/%s/settings".formatted(auth.getName()));

        return ok().build();
    }
}
