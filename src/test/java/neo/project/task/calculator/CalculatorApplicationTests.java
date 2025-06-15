package neo.project.task.calculator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CalculatorApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void testConstructor() {
		new CalculatorApplication();
	}

	@Test
	void testMainMethod() {
		String[] args = {};
		CalculatorApplication.main(args);
	}

}
