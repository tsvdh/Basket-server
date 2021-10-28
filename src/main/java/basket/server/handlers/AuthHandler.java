package basket.server.handlers;

import basket.server.model.User;
import basket.server.service.UserService;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component("authHandler")
@RequiredArgsConstructor
public class AuthHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        User user = userService.getByUsername(authentication.getName()).get();

        if (user.isDeveloper()) {
            response.sendRedirect("/developers/" + user.getUsername());
        } else {
            response.sendRedirect("/apps");
        }
    }
}
