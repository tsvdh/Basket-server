package basket.server.api;

import basket.server.model.App;
import basket.server.service.AppService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("apps")
@RestController
public class AppController {

    private final AppService appService;

    @Autowired
    public AppController(AppService appService) {
        this.appService = appService;
    }

    @GetMapping(path = "{name}")
    public App get(@PathVariable String name) {
        return appService.get(name);
    }

    @GetMapping
    public List<App> getAll() {
        return appService.getAll();
    }

    @GetMapping(path = "names") 
    public List<App> get(@RequestBody @NonNull @Validated List<String> names) {
        return appService.get(names);
    }

    @PostMapping
    public void add(@RequestBody @NonNull @Validated App app) {
        appService.add(app);
    }

    @PutMapping(path = "{name}")
    public void update(@PathVariable String name, @RequestBody @NonNull @Validated App newApp) {
        appService.update(name, newApp);
    }
}
