package pickup_shuttle.pickup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PickupApplication {

	public static void main(String[] args) {
		SpringApplication.run(PickupApplication.class, args);
	}

}
