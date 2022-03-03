package basket.server.dao.database.app;

import basket.server.model.App;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AppMongoRepository extends MongoRepository<App, String> {

    Optional<App> findAppByName(String name);

    List<App> findAppsByNameIn(Collection<String> name);
}
