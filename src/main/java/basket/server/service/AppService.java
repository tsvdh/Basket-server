package basket.server.service;

import basket.server.dao.database.app.AppDAO;
import basket.server.model.App;
import basket.server.model.input.FormApp;
import basket.server.util.IllegalActionException;
import java.util.Collection;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AppService {

    private final AppDAO appDAO;
    private final ValidationService validationService;
    private final UserService userService;

    @Autowired
    public AppService(@Qualifier("localAppDAO") AppDAO appDAO,
                      ValidationService validationService, UserService userService) {
        this.appDAO = appDAO;
        this.validationService = validationService;
        this.userService = userService;
    }

    public Optional<App> get(String name) {
        log.info("Getting app {}", name);
        return appDAO.get(name);
    }

    public Collection<App> getAll() {
        log.info("Getting all apps");
        return appDAO.getAll();
    }

    public Collection<App> get(Collection<String> names) {
        log.info("Getting apps {}", names);
        return appDAO.get(names);
    }

    public void add(FormApp formApp, String creatorName) throws IllegalActionException {
        log.info("Validating new app");

        var optionalCreator = userService.getByUsername(creatorName);
        App newApp = validationService.validateFormApp(formApp, optionalCreator);
        add(newApp);
    }

    public void add(App app) throws IllegalActionException {
        log.info("Adding new app {}", app.getName());
        appDAO.add(app);
    }

    public void update(FormApp formApp, String creatorName) throws IllegalActionException {
        log.info("Validating updated app");

        var optionalCreator = userService.getByUsername(creatorName);
        App updatedApp = validationService.validateFormApp(formApp, optionalCreator);
        update(updatedApp);
    }

    public void update(App updatedApp) throws IllegalActionException {
        log.info("Updating app {}", updatedApp.getName());
        appDAO.update(updatedApp);
    }
}
