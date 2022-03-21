package basket.server.controller.api;

import basket.server.model.App;
import basket.server.service.AppService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

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
}
