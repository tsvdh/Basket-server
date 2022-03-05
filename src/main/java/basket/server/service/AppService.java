package basket.server.service;

import basket.server.dao.database.app.AppDAO;
import basket.server.model.App;
import basket.server.util.BadRequestException;
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

    @Autowired
    public AppService(@Qualifier("localAppDAO") AppDAO appDAO) {
        this.appDAO = appDAO;
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

    public void add(App app) {
        log.info("Adding new app {}", app.getName());

        boolean success = appDAO.add(app);
        if (!success) {
            throw new BadRequestException("App already exists");
        }
    }

    public void update(App updatedApp) {
        log.info("Updating app {}", updatedApp.getName());

        boolean success = appDAO.update(updatedApp);
        if (!success) {
            throw new BadRequestException("Some of the updated values are already taken");
        }
    }
}
