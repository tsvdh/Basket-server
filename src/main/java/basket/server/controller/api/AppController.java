package basket.server.controller.api;

import basket.server.model.App;
import basket.server.service.AppService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RequestMapping( "api/v1/apps")
@RestController
@RequiredArgsConstructor
public class AppController {

    private final AppService appService;

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

    @PostMapping
    // @PreAuthorize("hasRole('DEVELOPER')")
    public ResponseEntity<Void> add(@RequestBody @NonNull @Validated App app) {
        boolean success = appService.add(app);

        if (success) {
            return ok().build();
        } else {
            return badRequest().build();
        }
    }

    @PutMapping
    @PreAuthorize("hasRole('DEVELOPER-' + updatedApp.getName())")
    public ResponseEntity<Void> update(@RequestBody @NonNull @Validated App updatedApp) {
        boolean success = appService.update(updatedApp);

        if (success) {
            return ok().build();
        } else {
            return status(CONFLICT).build();
        }
    }
}
