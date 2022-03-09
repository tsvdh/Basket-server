package basket.server;

import basket.server.dao.storage.DriveStorageDAO;
import basket.server.model.App;
import basket.server.model.AppStats;
import basket.server.model.DeveloperInfo;
import basket.server.model.Rating;
import basket.server.model.Release;
import basket.server.model.User;
import basket.server.service.AppService;
import basket.server.service.UserService;
import com.google.api.services.drive.model.File;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
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

    public static void main(String[] args) throws Exception {
        SpringApplication.run(BasketServerApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner run(UserService userService, PasswordEncoder passwordEncoder, AppService appService, DriveStorageDAO storageDAO) {
        return args -> {
            String pwd = passwordEncoder.encode("1");
            User user1 = new User("a@a.com", "userA", pwd, new HashSet<>(), false, null);
            user1.setId("beadsfewfa");
            User user2 = new User("b@b.com", "userB", pwd, new HashSet<>(), true,
                    new DeveloperInfo("B", "B", PhoneNumberUtil.getInstance().getExampleNumberForType("NL", PhoneNumberUtil.PhoneNumberType.MOBILE),
                            Set.of("app1", "app2"), Set.of("app1")));
            user2.setId("awefihoewh");

            userService.add(user1);
            userService.add(user2);

            App app1 = new App(
                    "app1",
                    "an app1",
                    new Release("1.0.0", LocalDate.EPOCH),
                    new Release("1.0.1", LocalDate.of(2018, 5, 2)),
                    "userB", Set.of("userB"),
                    new AppStats(Set.of("userA"), new Rating(42, 3))
            );
            app1.setId("sbdasdfa");

            App app2 = new App(
                    "app2",
                    "an app2",
                    new Release("2.0.0", LocalDate.ofEpochDay(10000)),
                    new Release("2.0.2", LocalDate.of(2019, 9, 24)),
                    "userB",
                    Set.of("userB"),
                    new AppStats(Set.of("userA"), new Rating(69, 4.2f))
            );
            app2.setId("bdfefwfgew");

            appService.add(app1);
            appService.add(app2);

            for (File file : storageDAO.getDrive().files().list().execute().getFiles()) {
                storageDAO.getDrive().files().delete(file.getId()).execute();
            }

            storageDAO.create("test");

            // var metadata = new File();
            // metadata.setName("test.txt");
            // var content = new FileContent("text/plain", new ClassPathResource("test.txt").getFile());
            //
            // storageDAO.getDrive().files().create(metadata, content).execute();
            //
            System.out.println(storageDAO.getDrive().files().list().execute());
            System.out.println(storageDAO.getDrive().about().get().setFields("storageQuota").execute().getStorageQuota());
        };
    }
}
