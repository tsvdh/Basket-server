package basket.server.api;

import basket.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

@Controller
@RequestMapping("api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final UserService userService;

    @GetMapping("username")
    public ResponseEntity<Boolean> availableUsername(@RequestHeader @NonNull String username) {
        if (username.isBlank()) {
            return badRequest().build();
        }
        return ok(userService.getByUsername(username).isEmpty());
    }
}
