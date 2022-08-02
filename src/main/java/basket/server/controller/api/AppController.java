package basket.server.controller.api;

import basket.server.model.app.App;
import basket.server.model.input.ReplaceFormApp;
import basket.server.service.AppService;
import basket.server.service.StorageService;
import basket.server.service.UserService;
import basket.server.util.HTMLUtil;
import basket.server.util.HTMLUtil.InputType;
import basket.server.util.IllegalActionException;
import basket.server.validation.validators.AppNameValidator;
import basket.server.validation.validators.DescriptionValidator;
import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.internalServerError;
import static org.springframework.http.ResponseEntity.ok;

@RequestMapping( "api/v1/app")
@RestController
@RequiredArgsConstructor
public class AppController {

    private final AppService appService;
    private final UserService userService;
    private final StorageService storageService;
    private final HTMLUtil htmlUtil;

    @GetMapping("get/one")
    public ResponseEntity<App> get(@RequestParam String appId) {
        var optionalApp = appService.getById(appId);
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
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Collection<App>> getUserLibrary(Principal principal) {
        var optionalUser = userService.getByUsername(principal.getName());
        if (optionalUser.isEmpty()) {
            return badRequest().build();
        }

        Set<String> appIds = optionalUser.get().getUserOf();
        return ok(appService.getByIds(appIds));
    }


    private void checkTaken(Optional<App> newApp, String oldAppName, List<String> faults) {
        if (newApp.isEmpty()) {
            return;
        }

        if (oldAppName.equals(newApp.get().getName())) {
            return;
        }

        faults.add("Already taken");
    }

    @ResponseBody
    @GetMapping(path = "html/valid/name", produces = TEXT_HTML_VALUE)
    public String getAppNameHTMLResponse(@RequestParam(name = "appName") String newAppName, @RequestParam String oldAppName,
                                         AppNameValidator appNameValidator,
                                         HttpServletRequest request, HttpServletResponse response) {
        List<String> faults;
        if (newAppName.equals("")) {
            faults = null;
        } else {
            faults = appNameValidator.getFaults(newAppName);
            checkTaken(appService.getByName(newAppName), oldAppName, faults);
        }

        return htmlUtil.getInputFragment(
                request, response,
                "appName",
                newAppName,
                faults,
                InputType.APP,
                "oldAppName", oldAppName
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

    @PostMapping("release")
    @PreAuthorize("hasRole('DEVELOPER/' + #appId)")
    public ResponseEntity<Void> release(@RequestParam String appId) throws IOException {
        var optionalApp = appService.getById(appId);
        if (optionalApp.isEmpty()) {
            return badRequest().build();
        }

        App app = optionalApp.get();

        if (!storageService.isReleasable(app.getId())) {
            return badRequest().build();
        }

        app.setAvailable(true);

        try {
            appService.update(app);
        } catch (IllegalActionException e) {
            return internalServerError().build();
        }

        return ok().build();
    }

    @PostMapping("info/change")
    @PreAuthorize("hasRole('DEVELOPER/' + #formApp.id)")
    public ResponseEntity<Void> changeInfo(@ModelAttribute ReplaceFormApp formApp,
                                           HttpServletResponse response) throws IOException {

        var optionalApp = appService.getById(formApp.getId());

        if (optionalApp.isEmpty()) {
            return badRequest().build();
        }

        App oldApp = optionalApp.get();

        try {
            appService.update(formApp, oldApp);
        } catch (IllegalActionException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        response.sendRedirect("/apps/%s/manage".formatted(formApp.getAppName()));

        return ok().build();
    }
}
