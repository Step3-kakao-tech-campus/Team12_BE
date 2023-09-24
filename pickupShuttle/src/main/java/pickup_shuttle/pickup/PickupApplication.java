package pickup_shuttle.pickup;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import pickup_shuttle.pickup.domain.user.User;
import pickup_shuttle.pickup.domain.user.UserRepository;

import java.util.Arrays;

//@SpringBootApplication
@EnableJpaAuditing
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class PickupApplication {

	public static void main(String[] args) {
		SpringApplication.run(PickupApplication.class, args);
	}

	// update 확인용 mock data
	@Profile("local")
	@Bean
	CommandLineRunner localServerStart(UserRepository userRepository) {
		return args -> {
			userRepository.saveAll(Arrays.asList(newUser("honggildong")));
		};
	}
	private User newUser(String uid){
		return User.builder()
				.uid(uid)
				.pwd("{bcrypt}$2a$10$8H0OT8wgtALJkig6fmypi.Y7jzI5Y7W9PGgRKqnVeS2cLWGifwHF2")
				.nickname("")
				.phoneNumber("010-0000-0000")
				.name("홍길동")
				.build();
	}


}