package basket.server.service;

import basket.server.dao.user.UserDAO;
import basket.server.model.User;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    private final UserDAO userDAO;

    @Autowired
    public UserService(@Qualifier("localUserDAO") UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public Optional<User> getById(String id) {
        log.info("Getting user with id {}", id);
        return userDAO.getById(id);
    }

    public Optional<User> getByUsername(String username) {
        log.info("Getting user {} by username", username);
        return userDAO.getByUsername(username);
    }

    public boolean add(User newUser) {
        log.info("Adding new user {}", newUser.getUsername());
        return userDAO.add(newUser);
    }

    public boolean update(User updatedUser) {
        log.info("Updating user {}", updatedUser.getUsername());
        return userDAO.update(updatedUser);
    }

    // @Override
    // public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    //     Optional<User> optionalUser = getByUsername(username);
    //     if (optionalUser.isEmpty()) {
    //         throw new UsernameNotFoundException("Could not find the specified username");
    //     }
    //
    //     User user = optionalUser.get();
    //     Set<GrantedAuthority> authorities = new HashSet<>();
    //
    //     return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getEncodedPassword(),
    //             authorities);
    // }
}
