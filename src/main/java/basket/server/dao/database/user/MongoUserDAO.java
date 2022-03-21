package basket.server.dao.database.user;

import basket.server.model.User;
import basket.server.util.IllegalActionException;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository("mongoUserDAO")
@RequiredArgsConstructor
public class MongoUserDAO implements UserDAO {

    private final UserMongoRepository userMongoRepository;

    @Override
    public Optional<User> getById(String id) {
        return userMongoRepository.findById(id);
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return userMongoRepository.findUserByEmail(email);
    }

    @Override
    public Optional<User> getByUsername(String username) {
        return userMongoRepository.findUserByUsername(username);
    }

    @Override
    public Optional<User> getByPhoneNumber(PhoneNumber phoneNumber) {
        return userMongoRepository.findUserByDeveloperInfoPhoneNumber(phoneNumber);
    }

    @Override
    public void add(User newUser) throws IllegalActionException {
        if (isUnique(newUser)) {
            userMongoRepository.save(newUser);
        } else {
            throw new IllegalActionException("Some of the values are already taken");
        }
    }

    @Override
    public void update(User updatedUser) throws IllegalActionException {
        if (validUpdate(updatedUser)) {
            userMongoRepository.save(updatedUser);
        } else {
            throw new IllegalActionException("Could not find user to update or some the new values are already taken");
        }
    }
}
