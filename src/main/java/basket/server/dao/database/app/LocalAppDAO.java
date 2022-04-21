package basket.server.dao.database.app;

import basket.server.model.app.App;
import basket.server.util.IllegalActionException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository("localAppDAO")
public class LocalAppDAO implements AppDAO {

    private static final List<App> localDB = new ArrayList<>();

    @Override
    public Optional<App> get(String name) {
        return localDB.stream()
                .filter(app -> app.getName().equals(name))
                .findFirst();
    }

    @Override
    public Collection<App> getAll() {
        return localDB;
    }

    @Override
    public Collection<App> get(Collection<String> names) {
        return localDB.stream()
                .filter(app -> names.contains(app.getName()))
                .toList();
    }

    @Override
    public void add(App newApp) throws IllegalActionException {
        if (get(newApp.getName()).isEmpty()) {
            localDB.add(newApp);
        } else {
            throw new IllegalActionException("App name already exists");
        }
    }

    @Override
    public void update(App updatedApp) throws IllegalActionException {
        for (int i = 0; i < localDB.size(); i++) {
            String curId = localDB.get(i).getId();
            String curName = localDB.get(i).getName();

            if (curId.equals(updatedApp.getId()) && curName.equals(updatedApp.getName())) {
                localDB.set(i, updatedApp);
                return;
            }
        }

        throw new IllegalActionException("Could not find app to update");
    }
}
