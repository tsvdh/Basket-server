package basket.server.dao.app;

import basket.server.model.App;
import java.util.ArrayList;
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
    public boolean add(App newApp) {
        if (get(newApp.getName()).isEmpty()) {
            localDB.add(newApp);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean update(App updatedApp) {
        for (int i = 0; i < localDB.size(); i++) {
            String curId = localDB.get(i).getId();
            String curName = localDB.get(i).getName();

            if (curId.equals(updatedApp.getId()) && curName.equals(updatedApp.getName())) {
                localDB.set(i, updatedApp);
                return true;
            }
        }
        return false;
    }
}
