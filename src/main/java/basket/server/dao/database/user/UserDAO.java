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

        boolean uniquePhoneNumber = !user.isDeveloper()
                || getByPhoneNumber(user.getDeveloperInfo().getPhoneNumber()).isEmpty();

        return uniqueId && uniqueUsername && uniqueEmail && uniquePhoneNumber;
    }

    void add(User newUser) throws IllegalActionException;

    default boolean validUpdate(User updatedUser) {
        var optionalUser = getById(updatedUser.getId());
        if (optionalUser.isEmpty()) {
            return false;
        }

        var oldUser = optionalUser.get();
        Optional<User> tempUser;

        // check if new value is unused or same as old one

        tempUser = getByUsername(updatedUser.getUsername());
        boolean validUsername = tempUser.isEmpty() || tempUser.get().equals(oldUser);

        tempUser = getByEmail(updatedUser.getEmail());
        boolean validEmail = tempUser.isEmpty() || tempUser.get().equals(oldUser);

        boolean validPhoneNumber;
        if (!updatedUser.isDeveloper()) {
            validPhoneNumber = true;
        } else {
            tempUser = getByPhoneNumber(updatedUser.getDeveloperInfo().getPhoneNumber());
            validPhoneNumber = tempUser.isEmpty() || tempUser.get().equals(oldUser);
        }

        return validUsername && validEmail && validPhoneNumber;
    }

    void update(User updatedUser) throws IllegalActionException;
}
