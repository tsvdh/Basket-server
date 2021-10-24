package basket.server.api;

import javax.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class WebController {

    @GetMapping("login")
    public String getLogin() {
        return "login";
    }

    @GetMapping("developers")
    public String getDevelopers(HttpSession session, Model model) {
        SecurityContext securityContext = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
        User user = (User) securityContext.getAuthentication().getPrincipal();
        model.addAttribute("username", user.getUsername());
        return "developers";
    }
}
