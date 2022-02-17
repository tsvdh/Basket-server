package basket.server;

import basket.server.model.DeveloperInfo;
import basket.server.model.User;
import basket.server.service.UserService;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import java.util.HashSet;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class BasketServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BasketServerApplication.class, args);
	}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner run(UserService userService, PasswordEncoder passwordEncoder) {
        return args -> {
            String pwd = passwordEncoder.encode("1");
            User user1 = new User("a@a.com", "userA", pwd, new HashSet<>(), false, null);
            user1.setId("beadsfewfa");
            User user2 = new User("b@b.com", "userB", pwd, new HashSet<>(), true,
                    new DeveloperInfo("B", "B", PhoneNumberUtil.getInstance().getExampleNumberForType("NL", PhoneNumberUtil.PhoneNumberType.MOBILE),
                            new HashSet<>(), new HashSet<>()));
            user2.setId("awefihoewh");

            userService.add(user1);
            userService.add(user2);
        };
    }
}
