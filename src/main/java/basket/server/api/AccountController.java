package basket.server.api;

import basket.server.model.User;
import basket.server.model.input.FormUser;
import basket.server.service.UserService;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Validator;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AccountController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;

    @GetMapping("api/v1/account/username")
    public ResponseEntity<Boolean> availableUsername(@RequestHeader @NotBlank String username) {
        return ok(userService.getByUsername(username).isEmpty());
    }

    @PutMapping("api/v1/account/email")
    public ResponseEntity<Void> verifyEmail(@RequestHeader @NotNull @Email String email) {


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
    public ResponseEntity<Void> register(@ModelAttribute FormUser formUser, HttpServletResponse response) throws IOException {

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
    public ResponseEntity<Void> test() {
        log.info("method executed");



        return ok().build();
    }
}
