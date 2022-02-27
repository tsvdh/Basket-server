package basket.server.service.database;

import basket.server.dao.app.AppDAO;
import basket.server.model.App;
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

    public boolean add(App app) {
        log.info("Adding new app {}", app.getName());
        return appDAO.add(app);
    }

    public boolean update(App updatedApp) {
        log.info("Updating app {}", updatedApp.getName());
        return appDAO.update(updatedApp);
    }
}
