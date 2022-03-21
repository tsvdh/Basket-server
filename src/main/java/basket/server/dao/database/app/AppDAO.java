package basket.server.dao.database.app;

import basket.server.model.App;
import basket.server.util.IllegalActionException;
import java.util.List;
import java.util.Optional;

public interface AppDAO {

    Optional<App> get(String name);

    List<App> getAll();

    List<App> get(List<String> names);

    void add(App newApp) throws IllegalActionException;

    void update(App updatedApp) throws IllegalActionException;
}
