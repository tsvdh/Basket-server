package basket.server.dao.database.app;

import basket.server.model.app.App;
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
    public Optional<App> getById(String id) {
        return mongoRepository.findById(id);
    }

    @Override
    public Optional<App> getByName(String name) {
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
        if (isUnique(newApp)) {
            mongoRepository.save(newApp);
        } else {
            throw new IllegalActionException("Some of the values are already taken");
        }
    }

    @Override
    public void update(App updatedApp) throws IllegalActionException {
        if (validUpdate(updatedApp)) {
            mongoRepository.save(updatedApp);
        } else {
            throw new IllegalActionException("Could not find user to update or some the new values are already taken");
        }
    }
}
