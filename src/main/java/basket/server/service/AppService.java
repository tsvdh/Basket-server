package basket.server.service;

import basket.server.dao.database.app.AppDAO;
import basket.server.model.App;
import basket.server.model.input.FormApp;
import basket.server.util.IllegalActionException;
import java.util.List;
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

    @Autowired
    public AppService(@Qualifier("localAppDAO") AppDAO appDAO,
                      ValidationService validationService) {
        this.appDAO = appDAO;
        this.validationService = validationService;
    }

    public Optional<App> get(String name) {
        log.info("Getting app {}", name);
        return appDAO.get(name);
    }

    public List<App> getAll() {
        log.info("Getting all apps");
        return appDAO.getAll();
    }

    public List<App> get(List<String> names) {
        log.info("Getting apps {}", names);
        return appDAO.get(names);
    }

    public void add(FormApp formApp, String creatorName) throws IllegalActionException {
        log.info("Validating new app");

        App newApp = validationService.validateFormApp(formApp, creatorName);
        add(newApp);
    }

    public void add(App app) throws IllegalActionException {
        log.info("Adding new app {}", app.getName());
        appDAO.add(app);
    }

    public void update(FormApp formApp, String creatorName) throws IllegalActionException {
        log.info("Validating updated app");

        App updatedApp = validationService.validateFormApp(formApp, creatorName);
        update(updatedApp);
    }

    public void update(App updatedApp) throws IllegalActionException {
        log.info("Updating app {}", updatedApp.getName());
        appDAO.update(updatedApp);
    }
}
