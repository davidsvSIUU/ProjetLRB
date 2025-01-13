package licence.projetlrb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("licence.projetlrb.Entities")
@EnableJpaRepositories("licence.projetlrb.Repositories")
public class ProjetLrbApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProjetLrbApplication.class, args);
	}
}