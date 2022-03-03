package basket.server.controller;

import basket.server.model.App;
import basket.server.model.User;
import basket.server.model.input.FormUser;
import basket.server.service.AppService;
import basket.server.service.UserService;
import basket.server.util.HTMLUtil;
import basket.server.service.ValidationService;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.ServletException;
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
    public String getRegistration(Model model, HttpServletRequest request) {
        model.addAttribute("formUser", new FormUser());
        model.addAttribute("countryCodeList", HTMLUtil.getCountryList());
        return "register";
    }

    @PostMapping("register")
    public ResponseEntity<Void> register(@ModelAttribute FormUser formUser,
                                         HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        boolean success = userService.add(formUser);
        if (!success) {
            return badRequest().build();
        }

        request.logout();
        request.login(formUser.getUsername(), formUser.getPassword());

        // if (user.isDeveloper()) {
        //     response.sendRedirect("/developers/" + user.getUsername());
        // } else {
        //     response.sendRedirect("/");
        // }

        return ok().build();
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

    @GetMapping("users/{pageUsername}")
    public ResponseEntity<Void> getUserPage(@PathVariable String pageUsername,
                                            HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getRequestURI() + "/home");

        return ok().build();
    }

    @GetMapping("users/{pageUsername}/home")
    public ModelAndView getUserHomePage(@PathVariable String pageUsername) {
        Optional<User> optionalUser = userService.getByUsername(pageUsername);

        if (optionalUser.isEmpty()) {
            ModelAndView errorView = new ModelAndView("error");
            errorView.setStatus(HttpStatus.NOT_FOUND);
            return errorView;
        }

        var modelAndView = new ModelAndView("user/home");

        modelAndView.addObject("pageUser", optionalUser.get());

        return modelAndView;
    }
}
