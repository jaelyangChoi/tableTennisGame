package my.tableTennisGame.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Table Tennis Game",
                version = "1.0.0",
                description = "탁구 게임 서비스 API 문서"
        )
)
class SwaggerConfig {
}
