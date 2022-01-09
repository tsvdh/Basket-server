package basket.server.dao.user;

import basket.server.model.User;
import java.util.Optional;

public interface UserDAO {

    Optional<User> getById(String id);

    Optional<User> getByEmail(String email);

    Optional<User> getByUsername(String username);

    Optional<User> getByPhoneNumber(String phoneNumber);

    default boolean isUnique(User user) {
        boolean uniqueId = user.getId() == null || getById(user.getId()).isEmpty();

        boolean uniqueUsername = getByUsername(user.getUsername()).isEmpty();

        boolean uniqueEmail = getByEmail(user.getEmail()).isEmpty();

        boolean uniquePhoneNumber = user.getDeveloperInfo() == null
                || getByPhoneNumber(user.getDeveloperInfo().getPhoneNumber()).isEmpty();

        return uniqueId && uniqueUsername && uniqueEmail && uniquePhoneNumber;
    }

    boolean add(User newUser);

    default boolean validUpdate(User updatedUser) {
        String id = updatedUser.getId();
        updatedUser.setId(null);

        boolean validUpdate = getById(id).isPresent() && isUnique(updatedUser);
        updatedUser.setId(id);

        return validUpdate;
    }

    boolean update(User updatedUser);
}
