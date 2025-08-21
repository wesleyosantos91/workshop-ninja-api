package br.org.soujava.bsb.api.infrastructure.openapi;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import br.org.soujava.bsb.api.api.v1.response.CustomProblemDetail;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.servers.Server;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!prod") // Não executa em produção por segurança
@Configuration
public class OpenAPIConfig {

    private static final String BAD_REQUEST_RESPONSE = "BadRequestResponse";
    private static final String NOT_FOUND_RESPONSE = "NotFoundResponse";
    private static final String NOT_ACCEPTABLE_RESPONSE = "NotAcceptableResponse";
    private static final String INTERNAL_SERVER_ERROR_RESPONSE = "InternalServerErrorResponse";

    @Value("${server.address:localhost}")
    private String host;

    @Value("${server.port:8080}")
    private Integer port;

    @Bean
    public OpenAPI openAPIDefinition() {

        final Info info = new Info()
                .title("Ninja API - Workshop SouJava BSB")
                .version("1.0.0")
                .contact(descriptionContact())
                .description("API RESTful de domínio ninja desenvolvida com Spring Boot - Workshop \"Do Zero à API\"")
                .termsOfService("https://soujava-brasilia.github.io/")
                .license(descriptionLicense());

        return new OpenAPI()
                .info(info)
                .components(components())
                .servers(List.of(getServer()));
    }

    private Contact descriptionContact() {
        return new Contact()
                .name("SouJava Brasília")
                .email("contato@soujava-brasilia.org")
                .url("https://soujava-brasilia.github.io/");
    }

    private License descriptionLicense() {
        return new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");
    }

    private Server getServer() {
        final Server devServer = new Server();
        devServer.setUrl(String.format("http://%s:%d", host, port));
        devServer.setDescription("Servidor de desenvolvimento");
        return devServer;
    }

    private Components components() {
        return new Components()
                .schemas(gerarSchemas())
                .responses(gerarResponses());
    }

    private Map<String, Schema> gerarSchemas() {
        // Registra o schema do CustomProblemDetail para erros
        return ModelConverters.getInstance().read(CustomProblemDetail.class);
    }

    private Map<String, ApiResponse> gerarResponses() {
        final Map<String, ApiResponse> apiResponseMap = new HashMap<>();

        final Content content = new Content()
                .addMediaType(APPLICATION_JSON_VALUE,
                        new MediaType().schema(new Schema<CustomProblemDetail>().$ref("CustomProblemDetail")));

        // Define respostas padrão para códigos de erro
        apiResponseMap.put(BAD_REQUEST_RESPONSE, new ApiResponse()
                .description("Requisição inválida - dados enviados estão incorretos")
                .content(content));

        apiResponseMap.put(NOT_FOUND_RESPONSE, new ApiResponse()
                .description("Recurso não encontrado")
                .content(content));

        apiResponseMap.put(NOT_ACCEPTABLE_RESPONSE, new ApiResponse()
                .description("Formato não aceito")
                .content(content));

        apiResponseMap.put(INTERNAL_SERVER_ERROR_RESPONSE, new ApiResponse()
                .description("Erro interno do servidor")
                .content(content));

        return apiResponseMap;
    }
}