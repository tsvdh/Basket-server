package basket.server.api;

import basket.server.model.App;
import basket.server.model.User;
import basket.server.service.AppService;
import basket.server.service.UserService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class WebController {

    private final UserService userService;
    private final AppService appService;

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
