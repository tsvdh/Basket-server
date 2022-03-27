package basket.server.dao.database.app;

import basket.server.model.App;
import basket.server.util.IllegalActionException;
import java.util.Collection;
import java.util.Optional;

public interface AppDAO {

    Optional<App> get(String name);

    Collection<App> getAll();

    Collection<App> get(Collection<String> names);

    void add(App newApp) throws IllegalActionException;

    void update(App updatedApp) throws IllegalActionException;
}
