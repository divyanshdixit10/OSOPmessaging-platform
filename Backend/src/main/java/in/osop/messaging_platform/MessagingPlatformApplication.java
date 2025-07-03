package in.osop.messaging_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(
	info = @Info(
		title = "Messaging Platform API",
		version = "1.0.0",
		description = "REST API for sending messages via Email, WhatsApp, and SMS"
	)
)
public class MessagingPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessagingPlatformApplication.class, args);
	}

}
