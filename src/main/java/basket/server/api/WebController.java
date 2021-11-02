package basket.server.api;

import basket.server.model.User;
import basket.server.service.UserService;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class WebController {

    private final UserService userService;

    @GetMapping("login")
    public String getLogin() {
        return "login";
    }

    @GetMapping("developers/{pageUsername}")
    public ModelAndView getDeveloperPage(@PathVariable String pageUsername, HttpServletRequest request) {
        Optional<User> optionalUser = userService.getByUsername(pageUsername);

        if (optionalUser.isEmpty() || !optionalUser.get().isDeveloper()) {
            ModelAndView errorView = new ModelAndView("error");
            errorView.setStatus(HttpStatus.NOT_FOUND);
            return errorView;
        }

        ModelAndView modelAndView = new ModelAndView("developer");

        modelAndView.addObject("pageUsername", pageUsername);

        return modelAndView;
    }

    @GetMapping("register")
    public String getRegistration() {
        return "register";
    }

    @GetMapping("api/v1/test")
    public boolean available(String username) {
        return userService.getByUsername(username).isEmpty();
    }
}
