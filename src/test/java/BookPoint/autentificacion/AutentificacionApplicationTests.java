package BookPoint.autentificacion;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AutentificacionApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void mainClassExist(){
		assertNotNull(AutentificacionApplication.class);
	}
}
