package in.osop.messaging_platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
    
    @Bean
    public WebClient whatsappWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.build();
    }
    
    @Bean
    public WebClient smsWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.build();
    }
} 