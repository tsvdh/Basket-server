package basket.server;

import basket.server.dao.storage.DriveStorageDAO;
import basket.server.dao.storage.LocalStorageDAO;
import basket.server.model.app.App;
import basket.server.model.app.AppStats;
import basket.server.model.app.Rating;
import basket.server.model.app.Release;
import basket.server.model.user.DeveloperInfo;
import basket.server.model.user.User;
import basket.server.service.AppService;
import basket.server.service.StorageService;
import basket.server.service.UserService;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
    CommandLineRunner run(UserService userService, PasswordEncoder passwordEncoder,
                          AppService appService, DriveStorageDAO storageDAO, StorageService storageService,
                          LocalStorageDAO localStorageDAO) {
        return args -> {
            String pwd = passwordEncoder.encode("a12341234");
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
                    "awefihoewh",
                    Set.of("awefihoewh"),
                    new AppStats(25234, new Rating(3.8f, Map.of("beadsfewfa", 4))),
                    true,
                    new Release("1.0.0", LocalDate.EPOCH),
                    new Release("1.0.1", LocalDate.of(2018, 5, 2))
            );
            app1.setId("sbdasdfa");

            App app2 = new App(
                    "app2",
                    "an app2",
                    "awefihoewh",
                    Set.of("awefihoewh"),
                    new AppStats(0, new Rating(null, new HashMap<>())),
                    false,
                    null,
                    null
            );
            app2.setId("bdfefwfgew");

            appService.add(app1);
            appService.add(app2);

            storageService.create("app1");
            storageService.create("app2");

            // for (File file : storageDAO.getDrive().files().list().execute().getFiles()) {
            //     storageDAO.getDrive().files().delete(file.getId()).execute();
            // }
            //
            // var stable = new ClassPathResource("testing/test.zip").getInputStream();
            // var experimental = new ClassPathResource("testing/test.zip").getInputStream();
            // var icon = new ClassPathResource("testing/random.png").getInputStream();
            //
            // storageService.create("test");
            //
            // storageService.upload("test", stable, FileName.STABLE, FileType.ZIP);
            // storageService.upload("test", experimental, FileName.EXPERIMENTAL, FileType.ZIP);
            // storageService.upload("test", icon, FileName.ICON, FileType.PNG);
            //
            // System.out.println(storageService.isComplete("test"));
            //
            // System.out.println(storageDAO.getDrive().files().list().execute());
            // System.out.println(storageDAO.getDrive().about().get().setFields("storageQuota").execute().getStorageQuota());

            // localStorageDAO.create("test1");
            // localStorageDAO.upload("test1", new ClassPathResource("testing/test.txt").getInputStream(), "test.txt", null);
            // IOUtils.copy(localStorageDAO.download("test1", "test.txt"), System.out);
        };
    }
}
