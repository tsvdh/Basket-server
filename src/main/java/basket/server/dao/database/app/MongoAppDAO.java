package basket.server.dao.database.app;

import basket.server.model.App;
import basket.server.util.IllegalActionException;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository("mongoAppDAO")
@RequiredArgsConstructor
public class MongoAppDAO implements AppDAO {

    private final AppMongoRepository mongoRepository;

    @Override
    public Optional<App> get(String name) {
        return mongoRepository.findAppByName(name);
    }

    @Override
    public Collection<App> getAll() {
        return mongoRepository.findAll();
    }

    @Override
    public Collection<App> get(Collection<String> names) {
        return mongoRepository.findAppsByNameIn(names);
    }

    @Override
    public void add(App newApp) throws IllegalActionException {
        if (get(newApp.getName()).isEmpty()) {
            mongoRepository.save(newApp);
        } else {
            throw new IllegalActionException("App name already exists");
        }
    }

    @Override
    public void update(App updatedApp) throws IllegalActionException {
        Optional<App> optionalApp = get(updatedApp.getName());

        if (optionalApp.isEmpty()) {
            throw new IllegalActionException("No app with the same name to update");
        }

        App oldApp = optionalApp.get();

        if (!oldApp.getId().equals(updatedApp.getId())) {
            throw new IllegalActionException("Old and new ids do not match");
        }

        mongoRepository.delete(oldApp);
        mongoRepository.save(updatedApp);
    }
}
