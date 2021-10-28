package basket.server.api;

import basket.server.service.UserService;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String getDeveloperPage(@PathVariable String pageUsername, HttpSession session, Model model) {
        if (userService.getByUsername(pageUsername).isEmpty()) {
            
        }

        model.addAttribute("pageUsername", pageUsername);

        SecurityContext securityContext = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
        User user = (User) securityContext.getAuthentication().getPrincipal();
        model.addAttribute("currentUsername", user.getUsername());

        return "developer";
    }

    @GetMapping("api/v1/test")
    public boolean available(String username) {
        return userService.getByUsername(username).isEmpty();
    }
}
