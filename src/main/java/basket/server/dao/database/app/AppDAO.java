package basket.server.dao.database.app;

import basket.server.model.App;
import java.util.List;
import java.util.Optional;

public interface AppDAO {

    Optional<App> get(String name);

    List<App> getAll();

    List<App> get(List<String> names);

    boolean add(App newApp);

    boolean update(App updatedApp);
}
