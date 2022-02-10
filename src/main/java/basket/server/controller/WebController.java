package basket.server.controller;

import basket.server.model.App;
import basket.server.model.User;
import basket.server.model.input.FormUser;
import basket.server.service.AppService;
import basket.server.service.UserService;
import basket.server.validation.ValidationService;
import com.neovisionaries.i18n.CountryCode;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class WebController {

    private final UserService userService;
    private final AppService appService;
    private final ValidationService validationService;

    @GetMapping("login")
    public String getLogin() {
        return "login";
    }

    @GetMapping("register")
    public String getRegistration(Model model) {
        model.addAttribute("formUser", new FormUser());
        model.addAttribute("countryCodeList",
                stream(CountryCode.values())
                        .sorted(comparing(CountryCode::getName))
                        .toList()
        );

        return "register";
    }

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

    @GetMapping("apps/{appName}")
    public ResponseEntity<App> getApp(@PathVariable String appName) {
        Optional<App> app = appService.get(appName);
        //noinspection OptionalIsPresent
        if (app.isPresent()) {
            return ok(app.get());
        } else {
            return notFound().build();
        }
    }

    @GetMapping("developers/{pageUsername}")
    public ModelAndView getDeveloperPage(@PathVariable String pageUsername) {
        Optional<User> optionalUser = userService.getByUsername(pageUsername);

        if (optionalUser.isEmpty() || !optionalUser.get().isDeveloper()) {
            ModelAndView errorView = new ModelAndView("error");
            errorView.setStatus(HttpStatus.NOT_FOUND);
            return errorView;
        }

        var modelAndView = new ModelAndView("developer");

        modelAndView.addObject("pageUsername", pageUsername);

        return modelAndView;
    }
}
