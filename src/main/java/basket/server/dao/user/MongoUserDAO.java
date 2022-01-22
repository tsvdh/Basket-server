package basket.server.dao.user;

import basket.server.model.User;
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
    public boolean add(User newUser) {
        if (isUnique(newUser)) {
            userMongoRepository.save(newUser);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean update(User updatedUser) {
        if (validUpdate(updatedUser)) {
            userMongoRepository.save(updatedUser);
            return true;
        } else {
            return false;
        }
    }
}
