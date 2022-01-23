package basket.server;

import basket.server.messaging.mail.EmailService;
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
    CommandLineRunner run(UserService userService, PasswordEncoder passwordEncoder, EmailService EMailService) {
        return args -> {
            String pwd = passwordEncoder.encode("1");
            User user1 = new User("a@a.com", "a", pwd, new HashSet<>(), false, null);
            user1.setId("beadsfewfa");
            User user2 = new User("b@b.com", "b", pwd, new HashSet<>(), true,
                    new DeveloperInfo("B", PhoneNumberUtil.getInstance().getExampleNumberForType("NL", PhoneNumberUtil.PhoneNumberType.MOBILE),
                            new HashSet<>(), new HashSet<>()));
            user2.setId("awefihoewh");
            User user3 = new User("c@c.com", "c", pwd, new HashSet<>(), false, null);
            user3.setId("fasdfeweii");

            userService.add(user1);
            userService.add(user2);
            userService.add(user3);

            // mailUtils.sendVerificationEmail("tsvdhurk@gmail.com");
        };
    }
}
