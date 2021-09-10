package basket.server.service;

import basket.server.dao.AppDAO;
import basket.server.model.App;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class AppService {

    private final AppDAO appDAO;

    @Autowired
    public AppService(@Qualifier("mongoDAO") AppDAO appDAO) {
        this.appDAO = appDAO;
    }

    public App get(String name) {
        return appDAO.get(name);
    }

    public List<App> getAll() {
        return appDAO.getAll();
    }

    public List<App> get(List<String> names) {
        return appDAO.get(names);
    }

    public void add(App app) {
        appDAO.add(app);
    }

    public void update(String name, App newApp) {
        appDAO.update(name, newApp);
    }
}
