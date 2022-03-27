package basket.server.controller.api;

import basket.server.model.App;
import basket.server.service.AppService;
import basket.server.service.UserService;
import basket.server.util.HTMLUtil;
import basket.server.util.HTMLUtil.InputType;
import basket.server.validation.validators.AppNameValidator;
import basket.server.validation.validators.DescriptionValidator;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

@RequestMapping( "api/v1/app")
@RestController
@RequiredArgsConstructor
public class AppController {

    private final AppService appService;
    private final UserService userService;
    private final HTMLUtil htmlUtil;

    @GetMapping("get/one")
    public ResponseEntity<App> get(@RequestParam String appName) {
        var optionalApp = appService.get(appName);
        if (optionalApp.isEmpty()) {
            return badRequest().build();
        } else {
            return ok(optionalApp.get());
        }
    }

    @GetMapping("get/all")
    public ResponseEntity<Collection<App>> getAll() {
        return ok(appService.getAll());
    }

    @GetMapping("get/user-library")
    public ResponseEntity<Collection<App>> getUserLibrary(@RequestParam String userName) {
        var optionalUser = userService.getByUsername(userName);
        if (optionalUser.isEmpty()) {
            return badRequest().build();
        }

        Set<String> apps = optionalUser.get().getUserOf();
        return ok(appService.get(apps));
    }


    @ResponseBody
    @GetMapping(path = "html/valid/name", produces = TEXT_HTML_VALUE)
    public String getAppNameHTMLResponse(@RequestParam String appName, AppNameValidator appNameValidator,
                                         HttpServletRequest request, HttpServletResponse response) {
        List<String> faults;
        if (appName.equals("")) {
            faults = null;
        } else {
            faults = appNameValidator.getFaults(appName);
            if (appService.get(appName).isPresent()) {
                faults.add("Already taken");
            }
        }

        return htmlUtil.getInputFragment(
                request, response,
                "appName",
                appName,
                faults,
                InputType.APP
        );
    }

    @ResponseBody
    @GetMapping(path = "html/valid/description", produces = TEXT_HTML_VALUE)
    public String getDescriptionHTMLResponse(@RequestParam String description,
                                             DescriptionValidator descriptionValidator,
                                             HttpServletRequest request, HttpServletResponse response) {
        List<String> faults;
        if (description.equals("")) {
            faults = null;
        } else {
            faults = descriptionValidator.getFaults(description);
        }

        return htmlUtil.getInputFragment(
                request, response,
                "description",
                description,
                faults,
                InputType.APP
        );
    }
}
