package ecommerce.weariva.weariva_ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableScheduling
public class WearivaEcommerceApplication {
	public static void main(String[] args) {

		// STORAGE CONFIGURATION
		System.setProperty("CLOUDINARY_URL",Dotenv.load().get("CLOUDINARY_URL"));

		// MAIL CONFIGURATION
		System.setProperty("MAIL_HOST",Dotenv.load().get("MAIL_HOST"));
		System.setProperty("MAIL_PORT",Dotenv.load().get("MAIL_PORT"));
		System.setProperty("USERNAME_MAIL",Dotenv.load().get("USERNAME_MAIL"));
		System.setProperty("PASSWORD_MAIL",Dotenv.load().get("PASSWORD_MAIL"));
		
		// DATABASE CONFIGURATION
		System.setProperty("DATABASE_USER",Dotenv.load().get("DATABASE_USER"));
		System.setProperty("DATABASE_PASSWORD",Dotenv.load().get("DATABASE_PASSWORD"));
		SpringApplication.run(WearivaEcommerceApplication.class, args);
	}
}
