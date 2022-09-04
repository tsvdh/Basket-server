package basket.server;

import basket.server.model.app.App;
import basket.server.model.app.AppStats;
import basket.server.model.app.Rating;
import basket.server.model.app.Release;
import basket.server.model.app.Release.Type;
import basket.server.model.user.AppUsage;
import basket.server.model.user.DeveloperInfo;
import basket.server.model.user.User;
import basket.server.service.AppService;
import basket.server.service.StorageService;
import basket.server.service.UserService;
import basket.server.util.types.storage.FileName;
import basket.server.util.types.storage.FileType;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
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
                          AppService appService, StorageService storageService) {
        return args -> {

            /*--- Mutation of objects in database through reference. Can only use local database. ---*/

            String pwd = passwordEncoder.encode("a12341234");

            User user1 = new User("a@a.com", "userA", pwd,
                    new HashMap<>(Map.of("app1ID", new AppUsage(Duration.ofMinutes(12), OffsetDateTime.now().minusDays(3)))), false, null);

            User user2 = new User("b@b.com", "userB", pwd,
                    new HashMap<>(), true,
                    new DeveloperInfo("B", "B", PhoneNumberUtil.getInstance().getExampleNumberForType("NL", PhoneNumberUtil.PhoneNumberType.MOBILE),
                            new HashSet<>(Set.of("app1ID", "app2ID")), new HashSet<>(Set.of("app1ID", "app2ID"))));

            userService.add(user1);
            user1.setId("user1ID");

            userService.add(user2);
            user2.setId("user2ID");

            App app1 = new App(
                    "app1",
                    "description of app 1",
                    "user2ID",
                    new HashSet<>(Set.of("user2ID")),
                    new AppStats(25234, new Rating(3.8f, Map.of("user1ID", 4))),
                    true,
                    new Release("1.0.0", OffsetDateTime.of(LocalDate.EPOCH, LocalTime.NOON, ZoneOffset.UTC), Type.STABLE),
                    new Release("1.0.1", OffsetDateTime.of(2018, 11, 23, 1, 1, 1, 1, ZoneOffset.UTC), Type.EXPERIMENTAL)
            );

            App app2 = new App(
                    "app2",
                    "description of app 2",
                    "user2ID",
                    new HashSet<>(Set.of("user2ID")),
                    new AppStats(0, new Rating(null, new HashMap<>())),
                    false,
                    null,
                    null
            );

            appService.add(app1);
            app1.setId("app1ID");

            appService.add(app2);
            app2.setId("app2ID");

            storageService.create("app1ID");
            storageService.create("app2ID");

            var icon = new ClassPathResource("testing/random.png");
            var stable = new ClassPathResource("testing/image.zip");
            var experimental = new ClassPathResource("testing/test.zip");

            storageService.upload("app1ID", icon.getInputStream(), FileName.ICON, FileType.PNG);
            storageService.upload("app1ID", stable.getInputStream(), FileName.STABLE, FileType.ZIP);
            storageService.upload("app1ID", experimental.getInputStream(), FileName.EXPERIMENTAL, FileType.ZIP);

            storageService.upload("app2ID", icon.getInputStream(), FileName.ICON, FileType.PNG);
            storageService.upload("app2ID", stable.getInputStream(), FileName.STABLE, FileType.ZIP);
            storageService.upload("app2ID", experimental.getInputStream(), FileName.EXPERIMENTAL, FileType.ZIP);

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
