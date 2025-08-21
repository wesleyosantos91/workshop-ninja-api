# Passo 9 - Documentação da API (Swagger/OpenAPI)

## O que vamos fazer
Vamos adicionar a dependência do SpringDoc OpenAPI, criar a configuração e testar a documentação automática da nossa API usando Swagger UI.

## 1) O que é Swagger/OpenAPI?

**Swagger UI** é uma interface web que:
- Mostra todos os endpoints da sua API
- Permite testar a API diretamente no navegador
- Gera documentação automática
- Facilita o entendimento da API para outros desenvolvedores

**OpenAPI** é o padrão de especificação que descreve APIs REST.

## 2) Adicionando a dependência SpringDoc OpenAPI

Primeiro, precisamos adicionar a dependência no `pom.xml`. Esta dependência não estava disponível no Spring Initializr, então vamos adicioná-la manualmente.

Adicione esta dependência na seção `<dependencies>` do seu `pom.xml`:

```xml
<!-- SpringDoc OpenAPI UI -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.6</version>
</dependency>
```

**Por que SpringDoc OpenAPI?**
- ✅ **Automático** - Gera documentação baseada no código
- ✅ **Padrão OpenAPI 3** - Segue especificação moderna
- ✅ **Integração Spring Boot** - Funciona nativamente com Spring
- ✅ **Interface interativa** - Permite testar endpoints

## 3) Configuração no application.yml

Além da dependência, precisamos adicionar algumas configurações no `application.yml` para personalizar o SpringDoc OpenAPI.

Adicione esta seção no seu `src/main/resources/application.yml`:

```yaml
# SpringDoc OpenAPI Configuration
springdoc:
  api-docs:
    path: /v3/api-docs
    enabled: true
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
    try-it-out-enabled: true
    operations-sorter: method
    tags-sorter: alpha
    doc-expansion: none
    disable-swagger-default-url: true
  show-actuator: false
  writer-with-default-pretty-printer: true
```

**Explicando as configurações:**
- `api-docs.path: /v3/api-docs` - Caminho para acessar a especificação OpenAPI em JSON
- `swagger-ui.path: /swagger-ui.html` - Caminho para acessar a interface Swagger UI
- `swagger-ui.enabled: true` - Habilita a interface Swagger UI
- `try-it-out-enabled: true` - Permite testar endpoints diretamente na UI
- `operations-sorter: method` - Ordena operações por método HTTP (GET, POST, etc.)
- `tags-sorter: alpha` - Ordena tags alfabeticamente
- `doc-expansion: none` - Não expande automaticamente as seções da documentação
- `disable-swagger-default-url: true` - Remove URL padrão do Swagger
- `show-actuator: false` - Não mostra endpoints do Spring Actuator
- `writer-with-default-pretty-printer: true` - Formata o JSON da API de forma legível

## 4) Criando a configuração OpenAPIConfig

Agora vamos criar a classe de configuração para personalizar nossa documentação:

Crie `src/main/java/br/org/soujava/bsb/api/infrastructure/openapi/OpenAPIConfig.java`:

```java
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
```

## 5) Entendendo a configuração

**@Profile("!prod")** - Só ativa em ambientes que não sejam produção (por segurança)

**@Configuration** - Marca como classe de configuração do Spring

**OpenAPI Bean** - Define as informações gerais da API:
- **Info** - Título, versão, descrição da API
- **Contact** - Informações de contato dos desenvolvedores
- **License** - Licença do projeto
- **Server** - URL do servidor

**Components** - Define componentes reutilizáveis:
- **Schemas** - Modelos de dados (como CustomProblemDetail)
- **Responses** - Respostas padrão para códigos de erro

**Por que registrar CustomProblemDetail?**
- Documenta automaticamente a estrutura de erros da API
- Mostra no Swagger como são as respostas de erro
- Mantém documentação sincronizada com implementação

## 6) Compilando e testando

1. **Compile o projeto** para baixar a dependência:
```bash
./mvnw compile
```

2. **Execute a aplicação:**
```bash
./mvnw spring-boot:run
```

3. **Acesse a documentação:**
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **JSON da API:** http://localhost:8080/v3/api-docs
- **YAML da API:** http://localhost:8080/v3/api-docs.yaml

## 7) O que você verá no Swagger

✅ **Informações da API** - Título, versão, descrição  
✅ **Todos os endpoints** - GET, POST, PUT, DELETE dos ninjas  
✅ **Modelos de dados** - Estrutura dos DTOs (Request/Response)  
✅ **Códigos de resposta** - 200, 201, 404, 400, etc.  
✅ **Validações documentadas** - Campos obrigatórios, limites, padrões
✅ **Possibilidade de teste** - Execute requests diretamente na interface

## 8) Melhorando a documentação com anotações

Você pode adicionar mais informações nos controllers usando anotações OpenAPI:

```java
@RestController
@RequestMapping("/api/v1/ninjas")
@Tag(name = "Ninjas", description = "Operações CRUD relacionadas aos ninjas")
public class NinjaController {

    @Operation(
        summary = "Lista todos os ninjas", 
        description = "Retorna uma lista com todos os ninjas cadastrados no sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping
    public ResponseEntity<List<NinjaResponse>> findAll() {
        // ...existing code...
    }

    @Operation(
        summary = "Cria um novo ninja",
        description = "Cadastra um novo ninja no sistema com os dados fornecidos"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Ninja criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<NinjaResponse> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados do ninja a ser criado",
                required = true
            )
            @Validated(Groups.Create.class) @RequestBody NinjaRequest request) {
        // ...existing code...
    }
}
```

## 9) Principais anotações OpenAPI

**@Tag** - Agrupa endpoints por categoria  
**@Operation** - Descreve o que o endpoint faz  
**@ApiResponses** - Lista possíveis códigos de resposta    
**@Parameter** - Descreve parâmetros da requisição  
**@RequestBody** - Descreve o corpo da requisição  
**@Schema** - Documenta campos dos DTOs

## 10) Adicionando exemplos nos DTOs

Você também pode melhorar a documentação dos DTOs:

```java
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Dados para criação ou atualização de um ninja")
public record NinjaRequest(
    
    @Schema(description = "Nome do ninja", example = "Naruto Uzumaki", maxLength = 100)
    @NotBlank(message = "Nome é obrigatório", groups = Groups.Create.class)
    String nome,
    
    @Schema(description = "Vila de origem", example = "Konoha", maxLength = 50)
    @NotBlank(message = "Vila é obrigatória", groups = Groups.Create.class)
    String vila,
    
    @Schema(description = "Nível de força do ninja", example = "85", minimum = "1", maximum = "100")
    @Min(value = 1, groups = Groups.Create.class) 
    @Max(value = 100, groups = Groups.Create.class)
    Integer nivelForca
    
    // ...outros campos...
) {}
```

## 11) Benefícios da documentação automática

✅ **Sempre atualizada** - Sincronizada automaticamente com o código  
✅ **Interativa** - Permite testar endpoints sem ferramentas externas  
✅ **Padronizada** - Segue especificação OpenAPI 3.0  
✅ **Completa** - Mostra estruturas, validações e exemplos  
✅ **Validações visíveis** - Documenta automaticamente as validações Bean Validation
✅ **Profissional** - Facilita integração com outros sistemas

## 12) Integração com Bean Validation

O SpringDoc OpenAPI automaticamente documenta as validações Bean Validation:

**Validações que aparecem automaticamente no Swagger:**
- `@NotNull`, `@NotBlank` → Campo aparece como **required**
- `@Size(max=100)` → Campo mostra **maxLength: 100**
- `@Min(1)`, `@Max(100)` → Campo mostra **minimum: 1, maximum: 100**
- `@Pattern(regexp="...")` → Campo mostra o padrão esperado
- `@Email` → Campo aparece como formato **email**

**Exemplo no Swagger UI:**
```json
{
  "nome": {
    "type": "string",
    "description": "Nome do ninja",
    "maxLength": 100,
    "example": "Naruto Uzumaki"
  },
  "nivel_forca": {
    "type": "integer", 
    "minimum": 1,
    "maximum": 100,
    "example": 85
  }
}
```

## Revisão Final - O que construímos

Parabéns! 🎉 Você construiu uma API REST completa do zero! Vamos revisar o que fizemos:

### 🏗️ **Arquitetura em Camadas**
- **Controller** - Recebe requisições HTTP
- **Service** - Processa lógica de negócio
- **Repository** - Acessa o banco de dados
- **Entity** - Representa tabelas do banco
- **DTOs** - Objetos para transferir dados

### 🛠️ **Tecnologias Utilizadas**
- **Java 21** - Linguagem moderna
- **Spring Boot 3.5.4** - Framework principal
- **Spring Data JPA** - Acesso ao banco de dados
- **H2 Database** - Banco de dados em memória
- **MapStruct** - Conversão automática entre objetos
- **Bean Validation** - Validação de dados com grupos
- **RFC 9457 Problem Details** - Tratamento padronizado de erros
- **SpringDoc OpenAPI** - Documentação automática

### 🎯 **Funcionalidades Implementadas**
- ✅ Criar ninja (POST) com validações
- ✅ Listar todos os ninjas (GET)
- ✅ Buscar ninja por ID (GET)
- ✅ Buscar ninjas com filtros (GET)
- ✅ Atualizar ninja (PUT) com validações
- ✅ Deletar ninja (DELETE)
- ✅ Tratamento de erros padronizado (RFC 9457)
- ✅ Validações Bean Validation com grupos
- ✅ Documentação automática e interativa

### 🚀 **Próximos Passos (para continuar aprendendo)**

**Segurança:**
- Spring Security com JWT
- Controle de acesso por roles

**Banco de dados real:**
- PostgreSQL/MySQL com Docker
- Migrations com Flyway

**Testes avançados:**
- Testcontainers para testes com banco real
- Testes de performance

**Observabilidade:**
- Spring Actuator
- Métricas com Micrometer

**Deploy:**
- Docker e Kubernetes
- CI/CD pipelines

### 📚 **O que você aprendeu**

1. ✅ **Estrutura bem organizada** (packages por funcionalidade)
2. ✅ **Separação de responsabilidades** (Controller → Service → Repository)
3. ✅ **Validação robusta** (Bean Validation com grupos)
4. ✅ **Tratamento de erros profissional** (RFC 9457)
5. ✅ **Documentação automática** (OpenAPI/Swagger)
6. ✅ **Mapeamento automático** (MapStruct)
7. ✅ **Testes automatizados** (Repository, Service, Controller)

## Próximo passo
Agora vamos finalizar com uma revisão geral do projeto e sugestões de melhorias para continuar evoluindo. **[STEP 9 — Revisão Final & Próximos Passos](README_STEP_9.md)**

