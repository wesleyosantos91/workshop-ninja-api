# Passo 6 - Controller (Endpoints da API)

## O que vamos fazer
Vamos criar o `NinjaController` que expõe os endpoints HTTP da nossa API REST e implementar testes automatizados completos.

## 1) O que é um Controller?

O **Controller** é a porta de entrada da sua API:
- Recebe requisições HTTP (GET, POST, PUT, DELETE)
- Chama o Service para processar a lógica
- Retorna respostas HTTP com dados ou status

**Fluxo completo:**
1. Cliente faz requisição → **Controller**
2. Controller chama → **Service** 
3. Service chama → **Repository**
4. Repository acessa → **Banco de Dados**
5. Resposta volta pelo mesmo caminho

## 2) Criando o NinjaController

Crie `src/main/java/br/org/soujava/bsb/api/api/v1/controller/NinjaController.java`:

```java
package br.org.soujava.bsb.api.api.v1.controller;

import static br.org.soujava.bsb.api.core.mapper.NinjaMapper.MAPPER;

import br.org.soujava.bsb.api.api.v1.request.NinjaQueryRequest;
import br.org.soujava.bsb.api.api.v1.request.NinjaRequest;
import br.org.soujava.bsb.api.api.v1.response.NinjaResponse;
import br.org.soujava.bsb.api.core.validation.Groups;
import br.org.soujava.bsb.api.domain.exception.ResourceNotFoundException;
import br.org.soujava.bsb.api.domain.service.NinjaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/ninjas")
public record NinjaController(NinjaService service) {

    private static final Logger LOGGER = LoggerFactory.getLogger(NinjaController.class);

    @PostMapping
    public ResponseEntity<NinjaResponse> create(@Validated(Groups.Create.class) @RequestBody NinjaRequest request) {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LOGGER.debug("Function started 'create ninja'");
        final var response = MAPPER.toResponse(service.create(request));
        stopWatch.stop();
        LOGGER.debug("finished function with sucess 'create ninja {}' in {} ms", response, stopWatch.getTotalTimeMillis());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<NinjaResponse> getById(@PathVariable Integer id) throws ResourceNotFoundException {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LOGGER.debug("Function started 'getById ninja' with id {}", id);
        final var ninja = service.findById(id);
        final var response = MAPPER.toResponse(ninja);
        stopWatch.stop();
        LOGGER.debug("finished function with sucess 'getById ninja' {} in {} ms", response, stopWatch.getTotalTimeMillis());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<PagedModel<NinjaResponse>> search(@ModelAttribute NinjaQueryRequest query, Pageable page) {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LOGGER.info("Function started 'find ninja'");
        final var pageEntity = service.search(query, page);
        stopWatch.stop();
        LOGGER.info("finished function with ninja 'find person' in {} ms", stopWatch.getTotalTimeMillis());

        return ResponseEntity.ok().body(new PagedModel<>(MAPPER.toPageResponse(pageEntity)));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<NinjaResponse> update(@PathVariable Integer id,
                                                @Validated(Groups.Update.class)
                                                @RequestBody NinjaRequest request) throws ResourceNotFoundException {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LOGGER.debug("Function started 'update ninja'");
        final var ninja = service.update(id, request);
        stopWatch.stop();
        LOGGER.debug("finished function with sucess 'update ninja' {} in {} ms", ninja, stopWatch.getTotalTimeMillis());
        return ResponseEntity.status(HttpStatus.OK).body(MAPPER.toResponse(ninja));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) throws ResourceNotFoundException {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LOGGER.debug("Function started 'delete ninja' with id {}", id);
        service.delete(id);
        stopWatch.stop();
        LOGGER.debug("finished function with sucess 'delete person' in {} ms", stopWatch.getTotalTimeMillis());

        return ResponseEntity.noContent().build();
    }
}
```

## 3) Entendendo as anotações e conceitos avançados

**@RestController** - Marca a classe como um controller REST
**@RequestMapping("/v1/ninjas")** - Prefixo de todas as rotas (sem `/api`)

**Record Class** - Controller implementado como record para imutabilidade

**Validação com Grupos:**
- `@Validated(Groups.Create.class)` - Valida apenas campos marcados para criação
- `@Validated(Groups.Update.class)` - Valida apenas campos marcados para atualização

**Monitoramento de Performance:**
- `StopWatch` - Mede tempo de execução dos métodos
- Logs estruturados para debugging e monitoramento

**Paginação:**
- `Pageable` - Parâmetros de paginação (page, size, sort)
- `PagedModel` - Resposta paginada com metadados

## 4) Endpoints criados

| Método | URL | Descrição |
|---------|-----|-----------|
| GET | `/v1/ninjas` | Lista ninjas com paginação e filtros |
| GET | `/v1/ninjas/1` | Busca ninja por ID |
| POST | `/v1/ninjas` | Cria novo ninja (com validação) |
| PUT | `/v1/ninjas/1` | Atualiza ninja existente |
| DELETE | `/v1/ninjas/1` | Deleta ninja |

## 5) Testando a API

Execute a aplicação e teste:

**No navegador (apenas GET):**
- http://localhost:8080/v1/ninjas
- http://localhost:8080/v1/ninjas/1

**No Swagger UI:**
- http://localhost:8080/swagger-ui.html

**Exemplo de POST com curl:**
```bash
curl -X POST http://localhost:8080/v1/ninjas \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Novo Ninja",
    "vila": "Konoha",
    "rank": "Genin",
    "chakra_tipo": "Fogo"
  }'
```

## 6) Testes Automatizados do Controller

Vamos criar testes completos que validam todos os cenários da API, incluindo validação Bean Validation e tratamento de erros.

### 6.1) Por que testar o Controller?

**Testes de Controller validam:**
- ✅ Endpoints funcionam corretamente
- ✅ Códigos de status HTTP estão corretos  
- ✅ JSON de resposta tem a estrutura esperada
- ✅ Validação Bean Validation funciona corretamente
- ✅ Tratamento de erros HTTP funciona
- ✅ Paginação e filtros funcionam

### 6.2) Diferença entre tipos de teste

| **Repository Test** | **Service Test** | **Controller Test** |
|-------------------|------------------|-------------------|
| Testa integração com banco | Testa lógica de negócio | Testa endpoints HTTP |
| Usa banco H2 real | Usa mocks | Usa MockMvc |
| `@DataJpaTest` | `@ExtendWith(MockitoExtension.class)` | `@WebMvcTest` |

### 6.3) Implementação completa dos testes

Crie `src/test/java/br/org/soujava/bsb/api/api/v1/controller/NinjaControllerTest.java`:

```java
package br.org.soujava.bsb.api.api.v1.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.org.soujava.bsb.api.api.v1.request.NinjaQueryRequest;
import br.org.soujava.bsb.api.api.v1.request.NinjaRequest;
import br.org.soujava.bsb.api.domain.entity.NinjaEntity;
import br.org.soujava.bsb.api.domain.exception.ResourceNotFoundException;
import br.org.soujava.bsb.api.domain.service.NinjaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(NinjaController.class)
@DisplayName("Ninja Controller")
class NinjaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NinjaService ninjaService;

    @Autowired
    private ObjectMapper objectMapper;

    private NinjaEntity ninjaEntity;
    private NinjaRequest ninjaRequest;

    @BeforeEach
    void setUp() {
        // Preparar dados de teste reutilizáveis
        ninjaEntity = new NinjaEntity();
        ninjaEntity.setId(1);
        ninjaEntity.setNome("Naruto Uzumaki");
        ninjaEntity.setVila("Konoha");
        ninjaEntity.setCla("Uzumaki");
        ninjaEntity.setRank("Kage");
        ninjaEntity.setChakraTipo("Vento");
        ninjaEntity.setEspecialidade("Ninjutsu");
        ninjaEntity.setKekkeiGenkai("Kurama (Bijuu)");
        ninjaEntity.setStatus("Ativo");
        ninjaEntity.setNivelForca(98);
        ninjaEntity.setDataRegistro(LocalDate.of(2024, 1, 1));

        ninjaRequest = new NinjaRequest(
                "Naruto Uzumaki",
                "Konoha",
                "Uzumaki",
                "Kage",
                "Vento",
                "Ninjutsu",
                "Kurama (Bijuu)",
                "Ativo",
                98,
                LocalDate.of(2024, 1, 1)
        );
    }

    @Test
    @DisplayName("POST /v1/ninjas - Deve criar ninja com sucesso")
    void deveCrearNinjaComSucesso() throws Exception {
        // Given: service retornará ninja criado
        when(ninjaService.create(any(NinjaRequest.class))).thenReturn(ninjaEntity);

        // When/Then: fazer requisição POST e verificar resposta
        mockMvc.perform(post("/v1/ninjas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ninjaRequest)))
                .andDo(print()) // Imprime detalhes da requisição/resposta (útil para debug)
                .andExpect(status().isCreated()) // Status 201
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("Naruto Uzumaki")))
                .andExpect(jsonPath("$.vila", is("Konoha")))
                .andExpect(jsonPath("$.rank", is("Kage")))
                .andExpect(jsonPath("$.chakra_tipo", is("Vento"))) // snake_case por causa do @JsonNaming
                .andExpect(jsonPath("$.nivel_forca", is(98)));
    }

    @Test
    @DisplayName("GET /v1/ninjas/{id} - Deve buscar ninja por ID com sucesso")
    void deveBuscarNinjaPorIdComSucesso() throws Exception {
        // Given: service retornará ninja encontrado
        when(ninjaService.findById(1)).thenReturn(ninjaEntity);

        // When/Then: fazer requisição GET por ID
        mockMvc.perform(get("/v1/ninjas/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk()) // Status 200
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("Naruto Uzumaki")))
                .andExpect(jsonPath("$.vila", is("Konoha")))
                .andExpect(jsonPath("$.rank", is("Kage")));
    }

    @Test
    @DisplayName("GET /v1/ninjas/{id} - Deve retornar erro 404 para ID inexistente")
    void deveRetornarErro404ParaIdInexistente() throws Exception {
        // Given: service lançará ResourceNotFoundException
        when(ninjaService.findById(999))
                .thenThrow(new ResourceNotFoundException("Not found registry with code 999"));

        // When/Then: fazer requisição com ID inexistente
        mockMvc.perform(get("/v1/ninjas/{id}", 999))
                .andDo(print())
                .andExpect(status().isNotFound()); // Status 404
    }

    @Test
    @DisplayName("GET /v1/ninjas - Deve fazer busca paginada com sucesso")
    void deveFazerBuscaPaginadaComSucesso() throws Exception {
        // Given: preparar dados paginados
        NinjaEntity sasuke = new NinjaEntity();
        sasuke.setId(2);
        sasuke.setNome("Sasuke Uchiha");
        sasuke.setVila("Konoha");
        sasuke.setRank("Jounin");

        List<NinjaEntity> ninjas = List.of(ninjaEntity, sasuke);
        Page<NinjaEntity> page = new PageImpl<>(ninjas, PageRequest.of(0, 10), 2);

        when(ninjaService.search(any(NinjaQueryRequest.class), any(Pageable.class))).thenReturn(page);

        // When/Then: fazer requisição GET com parâmetros de busca
        mockMvc.perform(get("/v1/ninjas")
                        .param("vila", "Konoha")
                        .param("status", "Ativo")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].nome", is("Naruto Uzumaki")))
                .andExpect(jsonPath("$.content[1].nome", is("Sasuke Uchiha")))
                .andExpect(jsonPath("$.page.totalElements", is(2)))
                .andExpect(jsonPath("$.page.size", is(10)));
    }

    @Test
    @DisplayName("PUT /v1/ninjas/{id} - Deve atualizar ninja com sucesso")
    void deveAtualizarNinjaComSucesso() throws Exception {
        // Given: ninja atualizado
        NinjaEntity ninjaAtualizado = new NinjaEntity();
        ninjaAtualizado.setId(1);
        ninjaAtualizado.setNome("Naruto Uzumaki - Atualizado");
        ninjaAtualizado.setVila("Konoha");
        ninjaAtualizado.setRank("Hokage");
        ninjaAtualizado.setNivelForca(99);

        when(ninjaService.update(eq(1), any(NinjaRequest.class))).thenReturn(ninjaAtualizado);

        NinjaRequest requestAtualizado = new NinjaRequest(
                "Naruto Uzumaki - Atualizado",
                "Konoha",
                "Uzumaki",
                "Hokage",
                "Vento",
                "Ninjutsu",
                "Kurama (Bijuu)",
                "Ativo",
                99,
                LocalDate.of(2024, 1, 1)
        );

        // When/Then: fazer requisição PUT
        mockMvc.perform(put("/v1/ninjas/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAtualizado)))
                .andDo(print())
                .andExpect(status().isOk()) // Status 200
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("Naruto Uzumaki - Atualizado")))
                .andExpect(jsonPath("$.nivel_forca", is(99)));
    }

    @Test
    @DisplayName("DELETE /v1/ninjas/{id} - Deve deletar ninja com sucesso")
    void deveDeletarNinjaComSucesso() throws Exception {
        // Given: service executará delete sem erro
        doNothing().when(ninjaService).delete(1);

        // When/Then: fazer requisição DELETE
        mockMvc.perform(delete("/v1/ninjas/{id}", 1))
                .andDo(print())
                .andExpect(status().isNoContent()); // Status 204
    }

    @Test
    @DisplayName("POST /v1/ninjas - Deve retornar erro 400 para JSON vazio devido à validação")
    void deveRetornarErro400ParaJsonVazioDevidoValidacao() throws Exception {
        // Given: request com JSON vazio - deve falhar na validação
        String jsonVazio = "{}";

        // When/Then: fazer requisição com JSON vazio e esperar erro de validação
        mockMvc.perform(post("/v1/ninjas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonVazio))
                .andDo(print())
                .andExpect(status().isBadRequest()) // Status 400 - falha na validação
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.title", is("Bad Request")))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("POST /v1/ninjas - Deve retornar erro 400 para dados inválidos")
    void deveRetornarErro400ParaDadosInvalidos() throws Exception {
        // Given: request com dados que não passam na validação
        NinjaRequest requestInvalido = new NinjaRequest(
                "", // nome vazio - falha @NotBlank
                "", // vila vazia - falha @NotBlank  
                "Uzumaki",
                "RankInvalido", // rank inválido - falha @Pattern
                "", // chakraTipo vazio - falha @NotBlank
                "Ninjutsu",
                "Kurama (Bijuu)",
                "StatusInvalido", // status inválido - falha @Pattern
                101, // nivelForca > 100 - falha @Max
                LocalDate.of(2025, 1, 1) // data futura - falha @PastOrPresent
        );

        // When/Then: fazer requisição com dados inválidos
        mockMvc.perform(post("/v1/ninjas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andDo(print())
                .andExpect(status().isBadRequest()) // Status 400 - falha na validação
                .andExpect(content().contentType("application/problem+json"));
    }

    @Test
    @DisplayName("Deve testar requisição com JSON malformado")
    void deveTestarRequisicaoComJsonMalformado() throws Exception {
        // When/Then: fazer requisição com JSON inválido
        mockMvc.perform(post("/v1/ninjas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"nome\": \"test\", \"malformed\": }")) // JSON inválido
                .andDo(print())
                .andExpect(status().isBadRequest()); // Status 400
    }

    @Test
    @DisplayName("Deve testar validação de Content-Type")
    void deveTestarValidacaoContentType() throws Exception {
        // When/Then: fazer requisição POST sem Content-Type correto
        mockMvc.perform(post("/v1/ninjas")
                        .contentType(MediaType.TEXT_PLAIN) // Content-Type incorreto
                        .content("dados inválidos"))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType()); // Status 415
    }

    @Test
    @DisplayName("Deve testar headers de resposta")
    void deveTestarHeadersDeResposta() throws Exception {
        // Given
        when(ninjaService.create(any(NinjaRequest.class))).thenReturn(ninjaEntity);

        // When/Then: verificar headers de resposta
        mockMvc.perform(post("/v1/ninjas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ninjaRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
```

### 6.4) Entendendo as anotações de teste de Controller

**@WebMvcTest(NinjaController.class)**
- Carrega apenas a camada web (controllers)
- Não carrega services, repositories ou banco
- Foca apenas no teste do controller específico

**@MockitoBean**
- Cria um mock e o adiciona ao contexto Spring
- Substitui o bean real pelo mock nos testes

**@Autowired MockMvc**
- Simula requisições HTTP sem subir um servidor real
- Permite testar endpoints de forma isolada

**@Autowired ObjectMapper**
- Converte objetos Java para JSON e vice-versa
- Usado para serializar objetos no corpo das requisições

### 6.5) Entendendo o MockMvc

**perform()** - Executa uma requisição HTTP simulada:
```java
mockMvc.perform(get("/v1/ninjas/1"))
```

**Métodos de requisição:**
- `get()` - Requisição GET
- `post()` - Requisição POST  
- `put()` - Requisição PUT
- `delete()` - Requisição DELETE

**Configurando requisições:**
```java
.contentType(MediaType.APPLICATION_JSON) // Tipo de conteúdo
.content(objectMapper.writeValueAsString(objeto)) // Corpo da requisição
.param("vila", "Konoha") // Parâmetro de query string
```

### 6.6) Validações com andExpect()

**Status HTTP:**
```java
.andExpect(status().isOk())        // 200 OK
.andExpect(status().isCreated())   // 201 CREATED
.andExpect(status().isNotFound())  // 404 NOT FOUND
.andExpect(status().isNoContent()) // 204 NO CONTENT
.andExpect(status().isBadRequest()) // 400 BAD REQUEST
```

**Conteúdo da resposta:**
```java
.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(content().contentType("application/problem+json"))
```

**JSON Path (validação do JSON):**
```java
.andExpect(jsonPath("$.nome", is("Naruto")))        // Campo específico
.andExpect(jsonPath("$", hasSize(2)))               // Tamanho de array
.andExpect(jsonPath("$.chakra_tipo", is("Vento")))  // Campo em snake_case
.andExpect(jsonPath("$.errors").isArray())          // Validar se é array
```

### 6.7) Entendendo JsonPath

JsonPath é uma linguagem para navegar em estruturas JSON:

```java
// Para um JSON: {"id": 1, "nome": "Naruto", "vila": "Konoha"}
jsonPath("$.id", is(1))              // Acessa campo "id"
jsonPath("$.nome", is("Naruto"))     // Acessa campo "nome"

// Para um array: [{"nome": "Naruto"}, {"nome": "Sasuke"}]
jsonPath("$", hasSize(2))            // Array tem 2 elementos
jsonPath("$[0].nome", is("Naruto"))  // Primeiro elemento do array
jsonPath("$[1].nome", is("Sasuke"))  // Segundo elemento

// Para resposta paginada:
jsonPath("$.content", hasSize(2))           // Conteúdo tem 2 itens
jsonPath("$.page.totalElements", is(10))    // Total de elementos
jsonPath("$.page.size", is(5))              // Tamanho da página
```

### 6.8) andDo(print()) - Debug dos testes

O `.andDo(print())` imprime detalhes da requisição e resposta, muito útil para debug:

```
MockHttpServletRequest:
      HTTP Method = POST
      Request URI = /v1/ninjas
       Parameters = {}
          Headers = [Content-Type:"application/json;charset=UTF-8"]
             Body = {"nome":"Naruto Uzumaki","vila":"Konoha"...}

MockHttpServletResponse:
           Status = 201
          Headers = [Content-Type:"application/json"]
             Body = {"id":1,"nome":"Naruto Uzumaki"...}
```

### 6.9) Cenários de teste implementados

✅ **Testes de Sucesso:**
- Criação de ninja com dados válidos (201)
- Busca por ID existente (200)
- Busca paginada com filtros (200)
- Atualização de ninja (200)
- Deleção de ninja (204)

✅ **Testes de Validação:**
- JSON vazio (400 - Bean Validation)
- Dados inválidos (400 - Bean Validation)
- JSON malformado (400 - Parse error)
- Content-Type incorreto (415)

✅ **Testes de Erro:**
- ID inexistente (404)
- Headers de resposta corretos

### 6.10) Executando os testes

```bash
# Executar todos os testes do Controller
mvn test -Dtest=NinjaControllerTest

# Executar teste específico
mvn test -Dtest=NinjaControllerTest#deveCrearNinjaComSucesso

# Ver resultados detalhados
mvn test -Dtest=NinjaControllerTest -Dspring.profiles.active=test
```

**Resultado esperado:**
```
Tests run: 17, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## 7) Códigos de resposta HTTP implementados

- **200 OK** - Busca e atualização bem-sucedida
- **201 CREATED** - Recurso criado com sucesso
- **204 NO CONTENT** - Deleção bem-sucedida
- **400 BAD REQUEST** - Validação falhou ou JSON inválido
- **404 NOT FOUND** - Recurso não encontrado
- **415 UNSUPPORTED MEDIA TYPE** - Content-Type incorreto

## Próximo Passo

✅ **Passo 6 Concluído!** Controller com testes completos implementados.

**Próximo:** [Passo 7 - Exception Handler](README_STEP_7.md) - Tratamento centralizado de erros
