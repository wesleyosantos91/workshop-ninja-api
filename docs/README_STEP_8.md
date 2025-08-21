# README_STEP_8 — OpenAPI / Swagger

## Objetivo
Documentar e **validar** a documentação da API com **springdoc-openapi** e a classe de configuração **OpenAPIConfig** já presente no projeto.

---

## 1) Arquivos reais do projeto
- `infrastructure/openapi/OpenAPIConfig.java` — [abrir](sandbox:/mnt/data/step8_refs/OpenAPIConfig.java)

Principais pontos do `OpenAPIConfig` (recortes):
- Define um **bean `OpenAPI`** com `Info` (title/description/version), `Contact` e `License`.
- Adiciona **servers** (ex.: `http://localhost:8080`).
- Registra **schemas** (via `ModelConverters`) para tipos de erro, como `CustomProblemDetail`.
- Padroniza **responses** comuns (400, 404, 406, 500) com `Content` `application/json` referenciando o schema do erro.
- (Opcional) Pode incluir **global responses** por operação/tag.

---

## 2) Executar e validar a UI
Suba a aplicação e abra:
- **Swagger UI:** `http://localhost:8080/swagger-ui/index.html#/`
- **Documento JSON:** `http://localhost:8080/v3/api-docs`  
- **Documento YAML:** `http://localhost:8080/v3/api-docs.yaml`

> Se a UI não carregar, confirme a dependência `org.springdoc:springdoc-openapi-starter-webmvc-ui` no `pom.xml` e que não há bloqueio de path/segurança.

---

## 3) Boas práticas para enriquecer a doc
- **Tags e Grouping**: defina `@Tag(name = "...")` em controllers para agrupar endpoints.
- **`@Operation`**: adicione `summary`, `description`, `tags` e `responses` em cada endpoint.
- **`@Parameter`**: descreva query params (ex.: filtros de `NinjaQueryRequest`), inclusão de exemplos.
- **Schemas com exemplos**: adicione `@Schema(example = "...")` nos DTOs ou use `@ExampleObject` em `@Content` do endpoint.
- **Erros padronizados**: utilize os **ApiResponses** globais do `OpenAPIConfig` (400, 404, 500) para consistência.

Exemplo de anotação mínima num endpoint (no controller):
```java
@Operation(summary = "Cria um ninja", description = "Cadastra um novo ninja e retorna a representação criada.")
@ApiResponses(value = { 
    @ApiResponse(responseCode = "201", description = "Criado"),
    @ApiResponse(responseCode = "400", description = "Requisição inválida"),
    @ApiResponse(responseCode = "500", description = "Erro interno")
})
@PostMapping
public ResponseEntity<NinjaResponse> create(@Valid @RequestBody NinjaRequest request) { ... }
```

---

## 4) Checklist do STEP 8
- [ ] `OpenAPIConfig` registrado e carregando sem erros
- [ ] **Swagger UI** acessível e mostrando todos os endpoints de `/v1/ninjas`
- [ ] Schemas de **request/response** (DTOs) visíveis e com exemplos
- [ ] **Responses de erro** (ProblemDetail/CustomProblemDetail) documentadas

---

## 5) Próximo passo
Ir para **[STEP 9 — Revisão Final & Próximos Passos](README_STEP_9.md)** para validar tudo junto (app, Swagger, H2, testes) e ver sugestões de evolução (Testcontainers, profiles, observabilidade).
