package basket.server.dao.database.user;

import basket.server.model.user.User;
import basket.server.util.IllegalActionException;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import java.util.Optional;

public interface UserDAO {

    Optional<User> getById(String id);

    Optional<User> getByEmail(String email);

    Optional<User> getByUsername(String username);

    Optional<User> getByPhoneNumber(PhoneNumber phoneNumber);

    default boolean isUnique(User user) {
        boolean uniqueId = user.getId() == null || getById(user.getId()).isEmpty();

        boolean uniqueUsername = getByUsername(user.getUsername()).isEmpty();

        boolean uniqueEmail = getByEmail(user.getEmail()).isEmpty();

        boolean uniquePhoneNumber = user.getDeveloperInfo() == null
                || getByPhoneNumber(user.getDeveloperInfo().getPhoneNumber()).isEmpty();

        return uniqueId && uniqueUsername && uniqueEmail && uniquePhoneNumber;
    }

    void add(User newUser) throws IllegalActionException;

    default boolean validUpdate(User updatedUser) {
        String id = updatedUser.getId();
        updatedUser.setId(null);

        boolean validUpdate = getById(id).isPresent() && isUnique(updatedUser);
        updatedUser.setId(id);

        return validUpdate;
    }

    void update(User updatedUser) throws IllegalActionException;
}
