package basket.server.dao.user;

import basket.server.model.User;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

import static java.util.Objects.requireNonNull;

@Repository("localUserDAO")
public class LocalUserDAO implements UserDAO {

    private static final List<User> localDB = new ArrayList<>();

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
    public Optional<User> getByPhoneNumber(PhoneNumber phoneNumber) {
        return localDB.stream()
            .filter(User::isDeveloper)
            .filter(user -> {
                if (user.isDeveloper()) {
                    requireNonNull(user.getDeveloperInfo());
                    return user.getDeveloperInfo().getPhoneNumber().equals(phoneNumber);
                } else {
                    return false;
                }
            })
            .findFirst();
    }

    @Override
    public boolean add(User newUser) {
        // ID of new user is generated by the database
        newUser.setId(UUID.randomUUID().toString());

        if (isUnique(newUser)) {
            localDB.add(newUser);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean update(User updatedUser) {
        if (validUpdate(updatedUser)) {
            for (int i = 0; i < localDB.size(); i++) {
                if (localDB.get(i).getId().equals(updatedUser.getId())) {
                    localDB.set(i, updatedUser);
                    break;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
