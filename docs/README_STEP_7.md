# README_STEP_7 — Tratamento de Erros (ApiExceptionHandler)

## Objetivo
Padronizar as respostas de erro com **`ApiExceptionHandler`**, usando **ProblemDetail** do Spring 6/Boot 3 e o wrapper `CustomProblemDetail` com lista de erros (`ErrorResponse`).

---

## 1) Arquivos reais do projeto
- `api/exception/ApiExceptionHandler.java` — [abrir](sandbox:/mnt/data/step7_refs/ApiExceptionHandler.java)
- `api/v1/response/CustomProblemDetail.java` — [abrir](sandbox:/mnt/data/step7_refs/CustomProblemDetail.java)
- `api/v1/response/ErrorResponse.java` — [abrir](sandbox:/mnt/data/step7_refs/ErrorResponse.java)

---

## 2) Estratégia
- Para **erros de validação** (`MethodArgumentNotValidException`), o handler cria um `CustomProblemDetail` com:
  - `status` = 400, `title` = `Bad Request`
  - `detail` com uma mensagem geral (i18n via `MessageSource`)
  - `errors`: lista de `{ field, message_error }` (snake_case via Jackson)
  - `timestamp` automático em `CustomProblemDetail`
- Para **recurso não encontrado** (`ResourceNotFoundException`), retorna `ProblemDetail` 404 com `title = Not Found` e `detail` = mensagem da exceção.

Trecho real (recortes):
```java
@ResponseStatus(HttpStatus.NOT_FOUND)
@ExceptionHandler(ResourceNotFoundException.class)
private ResponseEntity<ProblemDetail> handleResourceNotFound(..., ResourceNotFoundException ex) {
    final ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    pd.setTitle(HttpStatus.NOT_FOUND.getReasonPhrase());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(pd);
}
```

---

## 3) Payloads de exemplo

### 3.1 404 — Not Found
```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Not found regitstry with code 999",
  "instance": "/v1/ninjas/999"
}
```

### 3.2 400 — Validation Error (exemplo)
```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Um ou mais campos estão inválidos.",
  "timestamp": "2025-08-20T21:15:33.123Z",
  "errors": [
    { "field": "nome", "message_error": "não deve estar em branco" },
    { "field": "rank", "message_error": "tamanho deve estar entre 1 e 20" }
  ]
}
```

> A propriedade `instance` pode aparecer automaticamente dependendo da configuração do `ProblemDetail` e do servlet container.

---

## 4) Testes de erro (MockMvc)

Crie `src/test/java/.../api/exception/ApiExceptionHandlerTest.java` com 2 cenários:

### 4.1 GET inexistente → 404
```java
@WebMvcTest
class ApiExceptionHandlerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private NinjaService service;

    @Test
    void getInexistente_deveRetornar404ComProblemDetail() throws Exception {
        given(service.findById(999)).willThrow(new ResourceNotFoundException("Not found regitstry with code 999"));

        mvc.perform(get("/v1/ninjas/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.title").value("Not Found"))
            .andExpect(jsonPath("$.detail").value("Not found regitstry with code 999"));
    }
}
```

### 4.2 POST inválido → 400
Supondo validações em `NinjaRequest` (ex.: `@NotBlank`), envie um body inválido e verifique os campos de erro:

```java
@Test
void postComBodyInvalido_deveRetornar400ComListaDeErros() throws Exception {
    String bodyInvalido = "{ \"nome\": \"\", \"rank\": \"\" }";

    mvc.perform(post("/v1/ninjas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(bodyInvalido))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.title").value("Bad Request"))
        .andExpect(jsonPath("$.errors").isArray());
}
```

> Se ainda não houver anotações de validação em `NinjaRequest`, inclua no STEP 4/6 (`@NotBlank`, `@Size`, etc.) e adicione `@Valid` no controller.

---

## 5) Checklist do STEP 7
- [ ] `ApiExceptionHandler` cobre 404 e 400 (validação) no padrão ProblemDetail
- [ ] Payload contém `title`, `status`, `detail` e lista `errors` quando aplicável
- [ ] Testes 404/400 com `MockMvc` **verdes**

---

## 6) Próximo passo
Ir para **[STEP 8 — OpenAPI / Swagger](README_STEP_8.md)** para validar e apresentar a documentação gerada.
