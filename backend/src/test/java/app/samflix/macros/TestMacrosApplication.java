package app.samflix.macros;

import org.springframework.boot.SpringApplication;

public class TestMacrosApplication {

	public static void main(String[] args) {
		SpringApplication.from(MacrosApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
