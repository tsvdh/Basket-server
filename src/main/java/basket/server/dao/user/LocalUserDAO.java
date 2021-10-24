package basket.server.dao.user;

import basket.server.model.User;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository("localUserDAO")
public class LocalUserDAO implements UserDAO {

    private static final ArrayList<User> localDB = new ArrayList<>();

    @Override
    public Optional<User> getById(String id) {
        return localDB.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return localDB.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public Optional<User> getByUsername(String username) {
        return localDB.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public boolean add(User newUser) {
        if (getById(newUser.getId()).isEmpty()) {
            localDB.add(newUser);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean update(User updatedUser) {
        // check if unique field is already present
        if (getByEmail(updatedUser.getEmail()).isPresent()) {
            return false;
        }

        for (int i = 0; i < localDB.size(); i++) {
            if (localDB.get(i).getId().equals(updatedUser.getId())) {
                localDB.set(i, updatedUser);
                return true;
            }
        }
        return false;
    }
}
