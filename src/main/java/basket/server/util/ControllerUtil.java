package basket.server.util;

import basket.server.model.app.App;
import basket.server.model.user.User;
import basket.server.service.AppService;
import basket.server.service.UserService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
@RequiredArgsConstructor
public class ControllerUtil {

    private final AppService appService;
    private final UserService userService;

    public App getApp(String appName) throws HttpClientErrorException {
        Optional<App> optionalApp = appService.get(appName);

        if (optionalApp.isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND,
                    "App %s does not exist".formatted(appName));
        } else {
            return optionalApp.get();
        }
    }

    public User getUser(String userName) throws HttpClientErrorException {
        Optional<User> optionalUser = userService.getByUsername(userName);

        if (optionalUser.isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND,
                    "User %s does not exist".formatted(userName));
        } else {
            return optionalUser.get();
        }
    }
}
