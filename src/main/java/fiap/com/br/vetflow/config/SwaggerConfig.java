package fiap.com.br.vetflow.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "VetFlow API",
                description = "API REST para gestão da jornada contínua de saúde do pet — Challenge FIAP 2026 / VetFlow",
                version = "1.0"
        )
)
public class SwaggerConfig {}
