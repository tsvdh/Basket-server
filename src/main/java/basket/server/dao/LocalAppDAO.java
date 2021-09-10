package basket.server.dao;

import basket.server.model.App;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository("localDAO")
public class LocalAppDAO implements AppDAO {

    private static final List<App> localDB = new ArrayList<>();

    @Override
    public App get(String name) {
        return localDB.stream()
                .filter(app -> app.getName().equals(name))
                .findFirst()
                .orElseThrow();
    }

    @Override
    public List<App> getAll() {
        return localDB;
    }

    @Override
    public List<App> get(List<String> names) {
        return localDB.stream()
                .filter(app -> names.contains(app.getName()))
                .toList();
    }

    @Override
    public void add(App app) {
        localDB.add(app);
    }

    @Override
    public void update(String name, App newApp) {
        localDB.replaceAll(app -> {
            if (app.getName().equals(name)) {
                return newApp;
            } else {
                return app;
            }
        });
    }
}
