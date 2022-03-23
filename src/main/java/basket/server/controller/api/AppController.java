package basket.server.controller.api;

import basket.server.model.App;
import basket.server.service.AppService;
import basket.server.util.HTMLUtil;
import basket.server.validation.validators.AppNameValidator;
import basket.server.validation.validators.DescriptionValidator;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static basket.server.util.HTMLUtil.APP_INPUT_MODEL;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RequestMapping( "api/v1/app")
@RestController
@RequiredArgsConstructor
public class AppController {

    private final AppService appService;
    private final HTMLUtil htmlUtil;

    @GetMapping
    public List<App> getAll() {
        return appService.getAll();
    }

    @GetMapping("name")
    public ResponseEntity<App> get(@RequestHeader String name) {
        Optional<App> app = appService.get(name);
        //noinspection OptionalIsPresent
        if (app.isPresent()) {
            return ok(app.get());
        } else {
            return notFound().build();
        }
    }

    @GetMapping("names")
    public List<App> get(@RequestHeader @NonNull List<String> names) {
        return appService.get(names);
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
                appName,
                "appName",
                faults,
                APP_INPUT_MODEL
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
                description,
                "description",
                faults,
                APP_INPUT_MODEL
        );
    }
}
