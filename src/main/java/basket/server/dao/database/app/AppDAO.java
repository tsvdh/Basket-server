package basket.server.dao.database.app;

import basket.server.model.app.App;
import basket.server.util.IllegalActionException;
import java.util.Collection;
import java.util.Optional;

public interface AppDAO {

    Optional<App> getById(String id);

    Optional<App> getByName(String name);

    Collection<App> getAll();

    Collection<App> get(Collection<String> names);

    default boolean isUnique(App app) {
        boolean uniqueId = app.getId() == null || getById(app.getId()).isEmpty();

        boolean uniqueName = getByName(app.getName()).isEmpty();

        return uniqueId && uniqueName;
    }

    void add(App newApp) throws IllegalActionException;

    default boolean validUpdate(App updatedApp) {
        var optionalApp = getById(updatedApp.getId());

        if (optionalApp.isEmpty()) {
            return false;
        }

        var oldApp = optionalApp.get();
        Optional<App> tempApp;

        // check if new value is unused or same as old one

        tempApp = getByName(updatedApp.getName());
        boolean validName = tempApp.isEmpty() || tempApp.get().equals(oldApp);

        return validName;
    }

    void update(App updatedApp) throws IllegalActionException;
}
