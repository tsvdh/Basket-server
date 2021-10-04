package basket.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BasketServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BasketServerApplication.class, args);
	}

    // @Bean
    // PasswordEncoder passwordEncoder() {
    //     return new SCryptPasswordEncoder();
    // }

    // @Bean
    // CommandLineRunner run(AppService appService) {
    //     return args -> {
    //
    //     };
    // }
}
