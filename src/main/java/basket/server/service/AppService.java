package basket.server.service;

import basket.server.dao.database.app.AppDAO;
import basket.server.model.app.App;
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
        log.info("Getting pageApp '{}'", name);
        return appDAO.getByName(name);
    }

    public Collection<App> getAll() {
        log.info("Getting all apps");
        return appDAO.getAll();
    }

    public Collection<App> getAvailable() {
        log.info("Getting available apps");

        return appDAO.getAll().stream()
                .filter(App::isAvailable)
                .toList();
    }

    public Collection<App> get(Collection<String> names) {
        log.info("Getting apps '{}'", names);
        return appDAO.get(names);
    }

    public void add(FormApp formApp, String creatorName) throws IllegalActionException {
        log.info("Validating new pageApp");

        var optionalCreator = userService.getByUsername(creatorName);
        if (optionalCreator.isEmpty()) {
            throw new IllegalActionException("Creator does not exist");
        }

        App newApp = validationService.validate(formApp, optionalCreator.get());
        add(newApp);
    }

    public void add(App app) throws IllegalActionException {
        log.info("Adding new pageApp '{}'", app.getName());
        appDAO.add(app);
    }

    public void update(FormApp formApp, App oldApp, String creatorName) throws IllegalActionException {
        log.info("Validating updated pageApp");

        var optionalCreator = userService.getByUsername(creatorName);
        if (optionalCreator.isEmpty()) {
            throw new IllegalActionException("Creator does not exist");
        }

        App updatedApp = validationService.validate(formApp, optionalCreator.get());

        updatedApp.setId(oldApp.getId());

        update(updatedApp);
    }

    public void update(App updatedApp) throws IllegalActionException {
        log.info("Updating pageApp '{}'", updatedApp.getName());
        appDAO.update(updatedApp);
    }
}
