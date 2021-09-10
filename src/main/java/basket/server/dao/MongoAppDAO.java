package basket.server.dao;

import basket.server.model.App;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("mongoDAO")
public class MongoAppDAO implements AppDAO {

    private final AppMongoRepository mongoRepository;

    @Autowired
    public MongoAppDAO(AppMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public App get(String name) {
        return mongoRepository.findAppByName(name)
                .orElseThrow();
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
    public void add(App app) {
        mongoRepository.insert(app);
    }

    @Override
    public void update(String name, App newApp) {
        mongoRepository.delete(this.get(name));
        mongoRepository.insert(newApp);
    }
}
