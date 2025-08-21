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

@Profile("local")
@Configuration
public class OpenAPIConfig {

    private static final String BAD_REQUEST_RESPONSE = "BadRequestResponse";
    private static final String NOT_FOUND_RESPONSE = "NotFoundResponse";
    private static final String NOT_ACCEPTABLE_RESPONSE = "NotAcceptableResponse";
    private static final String INTERNAL_SERVER_ERROR_RESPONSE = "InternalServerErrorResponse";

    @Value("${server.address}")
    private String host;

    @Value("${server.port}")
    private Integer port;

    @Value("${server.ssl.enabled}")
    private Boolean isHttps;

    @Value("${spring.profiles.active}")
    private String activeProfile;


    @Bean
    public OpenAPI openAPIDefinition() {

        final Info info = new Info()
                .title("API - Transaction")
                .version("1.0.0")
                .contact(descriptionContact())
                .description("Desafio tecnico para vaga de engenheiro de software na empresa PicPay")
                .termsOfService("http://www.termsofservice.url")
                .license(descriptionLicense());

        return new OpenAPI().info(info).components(components()).servers(List.of(getServer()));
    }

    private Contact descriptionContact() {
        return new Contact()
                .name("Wesley Oliveira Santos")
                .email("wesleyosantos91@gmail.com")
                .url("https://wesleyosantos91.github.io/");
    }

    private License descriptionLicense() {
        return new License()
                .name("License")
                .url("https://github.com/wesleyosantos91/picpay-desafio-backend/blob/main/LICENSE");
    }

    private Server getServer() {
        final Server devServer = new Server();
        devServer.setUrl(String.format("%s://%s:%d", isHttps ? "https" : "http", host, port));
        devServer.setDescription("Server URL in " + activeProfile + " environment");
        return devServer;
    }

    private Components components() {
        return new Components().schemas(gerarSchemas()).responses(gerarResponses());
    }

    private Map<String, Schema> gerarSchemas() {
        return ModelConverters.getInstance().read(CustomProblemDetail.class);
    }

    private Map<String, ApiResponse> gerarResponses() {
        final Map<String, ApiResponse> apiResponseMap = new HashMap<>();

        final Content content = new Content()
                .addMediaType(APPLICATION_JSON_VALUE,
                        new MediaType().schema(new Schema<CustomProblemDetail>().$ref("CustomProblemDetail")));

        apiResponseMap.put(BAD_REQUEST_RESPONSE, new ApiResponse()
                .description("Bad Request")
                .content(content));

        apiResponseMap.put(NOT_FOUND_RESPONSE, new ApiResponse()
                .description("Not Found")
                .content(content));

        apiResponseMap.put(NOT_ACCEPTABLE_RESPONSE, new ApiResponse()
                .description("Not Acceptable")
                .content(content));

        apiResponseMap.put(INTERNAL_SERVER_ERROR_RESPONSE, new ApiResponse()
                .description("Internal Server Error")
                .content(content));

        return apiResponseMap;
    }
}