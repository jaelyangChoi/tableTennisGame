package my.tableTennisGame.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("external-api")
public class ExternalApiProperties {
    private FakerApi fakerApi;

    @Data
    public static class FakerApi {
        private String baseUrl;
        private String endpoint;
        private String locale;
    }
}
