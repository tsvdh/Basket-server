package basket.server.dao.app;

import basket.server.model.App;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("mongoAppDAO")
public class MongoAppDAO implements AppDAO {

    private final AppMongoRepository mongoRepository;

    @Autowired
    public MongoAppDAO(AppMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Optional<App> get(String name) {
        return mongoRepository.findAppByName(name);
    }

    @Override
    public List<App> getAll() {
        return mongoRepository.findAll();
    }

    @Override
    public List<App> get(List<String> names) {
        return mongoRepository.findAppsByNameIn(names);
    }

    @Override
    public boolean add(App newApp) {
        if (get(newApp.getName()).isEmpty()) {
            mongoRepository.save(newApp);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean update(App updatedApp) {
        Optional<App> optionalApp = get(updatedApp.getName());

        if (optionalApp.isEmpty()) {
            return false;
        }

        App oldApp = optionalApp.get();

        if (!oldApp.getId().equals(updatedApp.getId())) {
            return false;
        }

        mongoRepository.delete(oldApp);
        mongoRepository.save(updatedApp);
        return true;
    }
}
