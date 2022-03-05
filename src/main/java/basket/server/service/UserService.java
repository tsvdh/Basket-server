package basket.server.service;

import basket.server.dao.database.user.UserDAO;
import basket.server.model.User;
import basket.server.model.input.FormUser;
import basket.server.util.BadRequestException;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static java.util.Objects.requireNonNull;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    private final UserDAO userDAO;
    private final ValidationService validationService;

    @Autowired
    public UserService(@Qualifier("localUserDAO") UserDAO userDAO,
                       ValidationService validationService) {
        this.userDAO = userDAO;
        this.validationService = validationService;
    }

    public Optional<User> getById(String id) {
        log.info("Getting user with id {}", id);
        return userDAO.getById(id);
    }

    public Optional<User> getByEmail(String email) {
        log.info("Getting user with email {}", email);
        return userDAO.getByEmail(email);
    }

    public Optional<User> getByUsername(String username) {
        log.info("Getting user with username {}", username);
        return userDAO.getByUsername(username);
    }

    public Optional<User> getByPhoneNumber(PhoneNumber phoneNumber) {
        log.info("Getting user with phone number {}", phoneNumber);
        return userDAO.getByPhoneNumber(phoneNumber);
    }

    public void add(FormUser formUser) {
        log.info("Validating new user");

        User newUser = validationService.validateFormUser(formUser);
        add(newUser);
    }

    public void add(User newUser) {
        log.info("Adding new user {}", newUser.getUsername());

        boolean success = userDAO.add(newUser);
        if (!success) {
            throw new BadRequestException("User already exists");
        }
    }

    public void update(FormUser formUser) {
        log.info("Validating updated user");

        User updatedUser = validationService.validateFormUser(formUser);
        update(updatedUser);
    }

    public void update(User updatedUser) {
        log.info("Updating user {}", updatedUser.getUsername());

        boolean success = userDAO.update(updatedUser);
        if (!success) {
            throw new BadRequestException("Some of the updated values are already taken");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = getByUsername(username);
        optionalUser = optionalUser.isEmpty() ? getByEmail(username) : optionalUser;

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("Can not find specified username or email");
        }

        User user = optionalUser.get();
        Set<GrantedAuthority> authorities = new HashSet<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        if (user.isDeveloper()) {
            requireNonNull(user.getDeveloperInfo());

            authorities.add(new SimpleGrantedAuthority("ROLE_DEVELOPER"));

            for (String appName : user.getDeveloperInfo().getDeveloperOf()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_DEVELOPER-" + appName));
            }

            for (String appName : user.getDeveloperInfo().getAdminOf()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN-" + appName));
            }
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getEncodedPassword(),
                authorities);
    }
}
