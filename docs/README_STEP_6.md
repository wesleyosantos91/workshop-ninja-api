# Passo 6 - Controller (Endpoints da API)

## O que vamos fazer
Vamos criar o `NinjaController` que expõe os endpoints HTTP da nossa API REST.

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

import br.org.soujava.bsb.api.api.v1.request.NinjaQueryRequest;
import br.org.soujava.bsb.api.api.v1.request.NinjaRequest;
import br.org.soujava.bsb.api.api.v1.response.NinjaResponse;
import br.org.soujava.bsb.api.domain.service.NinjaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ninjas")
public class NinjaController {

    private final NinjaService ninjaService;

    public NinjaController(NinjaService ninjaService) {
        this.ninjaService = ninjaService;
    }

    // GET /api/v1/ninjas - BUSCAR TODOS
    @GetMapping
    public ResponseEntity<List<NinjaResponse>> findAll() {
        List<NinjaResponse> ninjas = ninjaService.findAll();
        return ResponseEntity.ok(ninjas);
    }

    // GET /api/v1/ninjas/1 - BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<NinjaResponse> findById(@PathVariable Integer id) {
        NinjaResponse ninja = ninjaService.findById(id);
        return ResponseEntity.ok(ninja);
    }

    // GET /api/v1/ninjas/search - BUSCAR COM FILTROS
    @GetMapping("/search")
    public ResponseEntity<List<NinjaResponse>> search(NinjaQueryRequest query) {
        List<NinjaResponse> ninjas = ninjaService.findByQuery(query);
        return ResponseEntity.ok(ninjas);
    }

    // POST /api/v1/ninjas - CRIAR NOVO
    @PostMapping
    public ResponseEntity<NinjaResponse> create(@RequestBody NinjaRequest request) {
        NinjaResponse ninja = ninjaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ninja);
    }

    // PUT /api/v1/ninjas/1 - ATUALIZAR
    @PutMapping("/{id}")
    public ResponseEntity<NinjaResponse> update(
            @PathVariable Integer id, 
            @RequestBody NinjaRequest request) {
        NinjaResponse ninja = ninjaService.update(id, request);
        return ResponseEntity.ok(ninja);
    }

    // DELETE /api/v1/ninjas/1 - DELETAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        ninjaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
```

## 3) Entendendo as anotações

**@RestController** - Marca a classe como um controller REST
**@RequestMapping("/api/v1/ninjas")** - Prefixo de todas as rotas

**Mapeamentos HTTP:**
- **@GetMapping** - Para buscar dados (GET)
- **@PostMapping** - Para criar dados (POST)  
- **@PutMapping** - Para atualizar dados (PUT)
- **@DeleteMapping** - Para deletar dados (DELETE)

**Parâmetros:**
- **@PathVariable** - Pega valor da URL (`/ninjas/{id}`)
- **@RequestBody** - Pega dados do corpo da requisição (JSON)
- **@RequestParam** - Pega parâmetros da query string (`?nome=Naruto`)

## 4) Códigos de resposta HTTP

- **200 OK** - Operação bem-sucedida
- **201 CREATED** - Recurso criado com sucesso
- **204 NO CONTENT** - Operação bem-sucedida, sem conteúdo de retorno
- **404 NOT FOUND** - Recurso não encontrado
- **500 INTERNAL SERVER ERROR** - Erro interno

## 5) Endpoints criados

| Método | URL | Descrição |
|---------|-----|-----------|
| GET | `/api/v1/ninjas` | Lista todos os ninjas |
| GET | `/api/v1/ninjas/1` | Busca ninja por ID |
| GET | `/api/v1/ninjas/search?vila=Konoha` | Busca com filtros |
| POST | `/api/v1/ninjas` | Cria novo ninja |
| PUT | `/api/v1/ninjas/1` | Atualiza ninja existente |
| DELETE | `/api/v1/ninjas/1` | Deleta ninja |

## 6) Testando a API

Execute a aplicação e teste:

**No navegador (apenas GET):**
- http://localhost:8080/api/v1/ninjas
- http://localhost:8080/api/v1/ninjas/1

**No Swagger UI:**
- http://localhost:8080/swagger-ui.html

**Exemplo de POST com curl:**
```bash
curl -X POST http://localhost:8080/api/v1/ninjas \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Novo Ninja",
    "vila": "Konoha",
    "rank": "Genin",
    "chakra_tipo": "Fogo"
  }'
```

## 7) Testando o Controller com NinjaControllerTest

Além dos testes manuais, vamos criar testes automatizados para o Controller. Testes de Controller são diferentes dos outros - eles simulam requisições HTTP e verificam as respostas.

### 7.1) Por que testar o Controller?

**Testes de Controller validam:**
- ✅ Endpoints funcionam corretamente
- ✅ Códigos de status HTTP estão corretos
- ✅ JSON de resposta tem a estrutura esperada
- ✅ Parâmetros da URL são processados corretamente
- ✅ Tratamento de erros HTTP funciona

### 7.2) Diferença entre tipos de teste

| **Repository Test** | **Service Test** | **Controller Test** |
|-------------------|------------------|-------------------|
| Testa integração com banco | Testa lógica de negócio | Testa endpoints HTTP |
| Usa banco H2 real | Usa mocks | Usa MockMvc |
| `@DataJpaTest` | `@ExtendWith(MockitoExtension.class)` | `@WebMvcTest` |

### 7.3) Criando a classe de teste

Crie o arquivo `src/test/java/br/org/soujava/bsb/api/api/v1/controller/NinjaControllerTest.java`:

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
import br.org.soujava.bsb.api.api.v1.response.NinjaResponse;
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

    private NinjaResponse ninjaResponse;
    private NinjaRequest ninjaRequest;

    @BeforeEach
    void setUp() {
        // Preparar dados de teste reutilizáveis
        ninjaResponse = new NinjaResponse(
                1,
                "Naruto Uzumaki",
                "Konoha",
                "Uzumaki",
                "Hokage",
                "Vento",
                "Ninjutsu",
                "Kurama (Bijuu)",
                "Ativo",
                98,
                LocalDate.of(2024, 1, 1)
        );

        ninjaRequest = new NinjaRequest(
                "Naruto Uzumaki",
                "Konoha",
                "Uzumaki",
                "Hokage",
                "Vento",
                "Ninjutsu",
                "Kurama (Bijuu)",
                "Ativo",
                98,
                LocalDate.of(2024, 1, 1)
        );
    }

    @Test
    @DisplayName("POST /api/v1/ninjas - Deve criar ninja com sucesso")
    void deveCrearNinjaComSucesso() throws Exception {
        // Given: service retornará ninja criado
        when(ninjaService.create(any(NinjaRequest.class))).thenReturn(ninjaResponse);

        // When/Then: fazer requisição POST e verificar resposta
        mockMvc.perform(post("/api/v1/ninjas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ninjaRequest)))
                .andDo(print()) // Imprime detalhes da requisição/resposta (útil para debug)
                .andExpect(status().isCreated()) // Status 201
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("Naruto Uzumaki")))
                .andExpect(jsonPath("$.vila", is("Konoha")))
                .andExpect(jsonPath("$.rank", is("Hokage")))
                .andExpect(jsonPath("$.chakra_tipo", is("Vento"))) // snake_case por causa do @JsonNaming
                .andExpect(jsonPath("$.nivel_forca", is(98)));
    }

    @Test
    @DisplayName("GET /api/v1/ninjas/{id} - Deve buscar ninja por ID com sucesso")
    void deveBuscarNinjaPorIdComSucesso() throws Exception {
        // Given: service retornará ninja encontrado
        when(ninjaService.findById(1)).thenReturn(ninjaResponse);

        // When/Then: fazer requisição GET por ID
        mockMvc.perform(get("/api/v1/ninjas/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk()) // Status 200
                .andExpected(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("Naruto Uzumaki")))
                .andExpect(jsonPath("$.vila", is("Konoha")))
                .andExpect(jsonPath("$.rank", is("Hokage")));
    }

    @Test
    @DisplayName("GET /api/v1/ninjas/{id} - Deve retornar erro 404 para ID inexistente")
    void deveRetornarErro404ParaIdInexistente() throws Exception {
        // Given: service lançará ResourceNotFoundException
        when(ninjaService.findById(999))
                .thenThrow(new ResourceNotFoundException("Ninja não encontrado com ID: 999"));

        // When/Then: fazer requisição com ID inexistente
        mockMvc.perform(get("/api/v1/ninjas/{id}", 999))
                .andDo(print())
                .andExpect(status().isNotFound()); // Status 404
    }

    @Test
    @DisplayName("GET /api/v1/ninjas - Deve buscar todos os ninjas com sucesso")
    void deveBuscarTodosOsNinjasComSucesso() throws Exception {
        // Given: service retornará lista de ninjas
        List<NinjaResponse> ninjas = List.of(ninjaResponse);
        when(ninjaService.findAll()).thenReturn(ninjas);

        // When/Then: fazer requisição GET para listar todos
        mockMvc.perform(get("/api/v1/ninjas"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("Naruto Uzumaki")))
                .andExpect(jsonPath("$[0].vila", is("Konoha")));
    }

    @Test
    @DisplayName("GET /api/v1/ninjas/search - Deve buscar com filtros")
    void deveBuscarComFiltros() throws Exception {
        // Given: service retornará ninjas filtrados
        List<NinjaResponse> ninjasKonoha = List.of(ninjaResponse);
        when(ninjaService.findByQuery(any(NinjaQueryRequest.class))).thenReturn(ninjasKonoha);

        // When/Then: fazer busca com filtros
        mockMvc.perform(get("/api/v1/ninjas/search")
                        .param("vila", "Konoha")
                        .param("status", "Ativo"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].vila", is("Konoha")))
                .andExpect(jsonPath("$[0].status", is("Ativo")));
    }

    @Test
    @DisplayName("PUT /api/v1/ninjas/{id} - Deve atualizar ninja com sucesso")
    void deveAtualizarNinjaComSucesso() throws Exception {
        // Given: ninja atualizado
        NinjaResponse ninjaAtualizado = new NinjaResponse(
                1,
                "Naruto Uzumaki - Hokage",
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

        when(ninjaService.update(eq(1), any(NinjaRequest.class))).thenReturn(ninjaAtualizado);

        // When/Then: fazer requisição PUT
        mockMvc.perform(put("/api/v1/ninjas/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ninjaRequest)))
                .andDo(print())
                .andExpect(status().isOk()) // Status 200
                .andExpect(jsonPath("$.nome", is("Naruto Uzumaki - Hokage")))
                .andExpect(jsonPath("$.nivel_forca", is(99)));
    }

    @Test
    @DisplayName("PUT /api/v1/ninjas/{id} - Deve retornar erro 404 para ID inexistente na atualização")
    void deveRetornarErro404ParaAtualizacaoComIdInexistente() throws Exception {
        // Given: service lançará exceção para ID inexistente
        when(ninjaService.update(eq(999), any(NinjaRequest.class)))
                .thenThrow(new ResourceNotFoundException("Ninja não encontrado com ID: 999"));

        // When/Then: tentar atualizar ninja inexistente
        mockMvc.perform(put("/api/v1/ninjas/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ninjaRequest)))
                .andDo(print())
                .andExpect(status().isNotFound()); // Status 404
    }

    @Test
    @DisplayName("DELETE /api/v1/ninjas/{id} - Deve deletar ninja com sucesso")
    void deveDeletarNinjaComSucesso() throws Exception {
        // Given: service executará deleção sem erro
        doNothing().when(ninjaService).deleteById(1);

        // When/Then: fazer requisição DELETE
        mockMvc.perform(delete("/api/v1/ninjas/{id}", 1))
                .andDo(print())
                .andExpect(status().isNoContent()); // Status 204
    }

    @Test
    @DisplayName("DELETE /api/v1/ninjas/{id} - Deve retornar erro 404 para ID inexistente na deleção")
    void deveRetornarErro404ParaDelecaoComIdInexistente() throws Exception {
        // Given: service lançará exceção para ID inexistente
        doThrow(new ResourceNotFoundException("Ninja não encontrado com ID: 999"))
                .when(ninjaService).deleteById(999);

        // When/Then: tentar deletar ninja inexistente
        mockMvc.perform(delete("/api/v1/ninjas/{id}", 999))
                .andDo(print())
                .andExpect(status().isNotFound()); // Status 404
    }
}
```

### 7.4) Entendendo as anotações de teste de Controller

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

### 7.5) Entendendo o MockMvc

**perform()** - Executa uma requisição HTTP simulada:
```java
mockMvc.perform(get("/api/v1/ninjas/1"))
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

### 7.6) Validações com andExpect()

**Status HTTP:**
```java
.andExpect(status().isOk())        // 200 OK
.andExpect(status().isCreated())   // 201 CREATED
.andExpect(status().isNotFound())  // 404 NOT FOUND
.andExpect(status().isNoContent()) // 204 NO CONTENT
```

**Conteúdo da resposta:**
```java
.andExpected(content().contentType(MediaType.APPLICATION_JSON))
```

**JSON Path (validação do JSON):**
```java
.andExpect(jsonPath("$.nome", is("Naruto")))        // Campo específico
.andExpect(jsonPath("$", hasSize(2)))               // Tamanho de array
.andExpect(jsonPath("$.chakra_tipo", is("Vento")))  // Campo em snake_case
```

### 7.7) Entendendo JsonPath

JsonPath é uma linguagem para navegar em estruturas JSON:

```java
// Para um JSON: {"id": 1, "nome": "Naruto", "vila": "Konoha"}
jsonPath("$.id", is(1))        // Acessa campo "id"
jsonPath("$.nome", is("Naruto")) // Acessa campo "nome"

// Para um array: [{"nome": "Naruto"}, {"nome": "Sasuke"}]
jsonPath("$", hasSize(2))           // Array tem 2 elementos
jsonPath("$[0].nome", is("Naruto")) // Primeiro elemento do array
jsonPath("$[1].nome", is("Sasuke")) // Segundo elemento
```

### 7.8) andDo(print()) - Debug dos testes

O `.andDo(print())` imprime detalhes da requisição e resposta, muito útil para debug:

```
MockHttpServletRequest:
      HTTP Method = GET
      Request URI = /api/v1/ninjas/1
      
MockHttpServletResponse:
           Status = 200
    Content-Type = application/json
            Body = {"id":1,"nome":"Naruto Uzumaki","vila":"Konoha"...}
```

### 7.9) Executando os testes

**No terminal:**
```bash
# Executa todos os testes
./mvnw test

# Executa apenas testes do Controller
./mvnw test -Dtest=NinjaControllerTest
```

### 7.10) O que cada teste valida

1. **deveCrearNinjaComSucesso()** - POST cria ninja e retorna 201
2. **deveBuscarNinjaPorIdComSucesso()** - GET por ID retorna ninja
3. **deveRetornarErro404ParaIdInexistente()** - GET com ID inválido retorna 404
4. **deveBuscarTodosOsNinjasComSucesso()** - GET lista todos os ninjas
5. **deveBuscarComFiltros()** - GET com parâmetros filtra corretamente
6. **deveAtualizarNinjaComSucesso()** - PUT atualiza ninja existente
7. **deveRetornarErro404ParaAtualizacaoComIdInexistente()** - PUT com ID inválido retorna 404
8. **deveDeletarNinjaComSucesso()** - DELETE remove ninja e retorna 204
9. **deveRetornarErro404ParaDelecaoComIdInexistente()** - DELETE com ID inválido retorna 404

### 7.11) Benefícios dos testes de Controller

✅ **Garantia de funcionamento** - Endpoints funcionam como esperado
✅ **Códigos HTTP corretos** - Status codes apropriados para cada situação  
✅ **Estrutura JSON validada** - Resposta tem formato correto
✅ **Rapidez** - Não precisa subir servidor real
✅ **Cobertura completa** - Testa cenários de sucesso e falha

## Próximo passo
Agora vamos criar o tratamento global de exceções para melhorar as respostas de erro da API. **[STEP 7 — Tratamento de Erros](README_STEP_7.md)**
