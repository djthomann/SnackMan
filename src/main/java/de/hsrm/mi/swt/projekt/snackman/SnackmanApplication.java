package de.hsrm.mi.swt.projekt.snackman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SnackmanApplication {

	public static void main(String[] args) {
		System.setProperty("python.import.site", "false");
		SpringApplication.run(SnackmanApplication.class, args);
	}

}
