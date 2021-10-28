package basket.server;

import basket.server.model.User;
import basket.server.service.UserService;
import java.util.HashSet;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class BasketServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BasketServerApplication.class, args);
	}

    @Bean
    CommandLineRunner run(UserService userService, PasswordEncoder passwordEncoder) {
        return args -> {
            String pwd = passwordEncoder.encode("1");
            User user1 = new User("a@a.com", "a", pwd, new HashSet<>(), false, null);
            user1.setId("beadsfewfa");
            User user2 = new User("b@b.com", "b", pwd, new HashSet<>(), true, null);
            user2.setId("awefihoewh");
            User user3 = new User("c@c.com", "c", pwd, new HashSet<>(), true, null);
            user3.setId("fasdfeweii");

            userService.add(user1);
            userService.add(user2);
            userService.add(user3);
        };
    }
}
