package basket.server.handlers;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component("authHandler")
public class AuthHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        User user = (User) authentication.getPrincipal();

        if (user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_DEVELOPER"))) {
            response.sendRedirect("/developers/" + user.getUsername());
        } else {
            response.sendRedirect("/apps");
        }
    }
}
