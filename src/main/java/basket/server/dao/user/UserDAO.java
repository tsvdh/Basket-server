package basket.server.dao.user;

import basket.server.model.User;
import java.util.Optional;

public interface UserDAO {

    Optional<User> getById(String id);

    Optional<User> getByUsername(String username);

    boolean add(User newUser);

    boolean update(User updatedUser);
}
