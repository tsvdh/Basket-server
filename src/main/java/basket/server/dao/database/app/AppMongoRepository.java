package basket.server.dao.database.app;

import basket.server.model.app.App;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AppMongoRepository extends MongoRepository<App, String> {

    Optional<App> findAppByName(String name);

    Collection<App> findAppsByIdIn(Collection<String> ids);
}
