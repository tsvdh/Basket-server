package basket.server.dao;

import basket.server.model.App;
import java.util.List;

public interface AppDAO {

    App get(String name);

    List<App> getAll();

    List<App> get(List<String> names);

    void add(App app);

    void update(String name, App newApp);
}
