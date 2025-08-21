# README_STEP_9 — Revisão Final & Próximos Passos

## Objetivo
Consolidar tudo que foi feito nas etapas anteriores, validar que **o projeto está íntegro** (app sobe, docs aparecem, banco sementeado, testes verdes) e indicar **evoluções recomendadas** para o repositório.

---

## 1) Checklist Final

### Execução e endpoints
- [ ] Aplicação sobe com `./mvnw spring-boot:run` sem erros
- [ ] **H2 Console** acessível em `http://localhost:8080/h2`  
  - JDBC URL: `jdbc:h2:mem:naruto;MODE=MYSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`, user: `sa`
- [ ] **Swagger UI** acessível em `http://localhost:8080/swagger-ui/index.html#/`
- [ ] **CRUD /v1/ninjas** responde conforme o contrato:
  - `POST /v1/ninjas` → **201 Created** com **Location** `/v1/ninjas/{id}`
  - `GET /v1/ninjas/{id}` → **200 OK** (ou **404** se não existir)
  - `PUT /v1/ninjas/{id}` → **200 OK** (ou **404**)
  - `DELETE /v1/ninjas/{id}` → **204 No Content**
  - `GET /v1/ninjas` → **200 OK** com paginação

### Persistência e mapeamentos
- [ ] `schema.sql` cria as tabelas esperadas (STEP 2)
- [ ] `data.sql` insere registros mínimos (STEP 2)
- [ ] `NinjaEntity` mapeada para a tabela `NINJA` e alinhada ao DDL (STEP 3)
- [ ] `NinjaRepository` opera com `Integer` como chave e cobre operações básicas (STEP 3)

### DTOs e serialização
- [ ] `NinjaRequest`, `NinjaQueryRequest`, `NinjaResponse` com **snake_case** no JSON (STEP 4)
- [ ] `NinjaMapper` converte **DTO ↔ Entity** e lista/página (STEP 4)

### Service e Controller
- [ ] `NinjaService` implementa `create`, `findById`, `update`, `delete`, `search` (STEP 5)
- [ ] `NinjaController` publica `/v1/ninjas` com os endpoints documentados (STEP 6)

### Erros e documentação
- [ ] `ApiExceptionHandler` retorna **ProblemDetail** (400/404) padronizado (STEP 7)
- [ ] `OpenAPIConfig` registra Info/Servers/Responses e a doc abre (STEP 8)

### Testes
- [ ] **Unitários/integração** executam com `./mvnw test` (verde)
- [ ] Cobertura mínima dos testes: repositório, service, controller e erros

---

## 2) Troubleshooting (erros comuns)

### App não sobe / porta ocupada
- **Sintoma:** `Port 8080 is already in use`
- **Como resolver:** alterar `server.port` no `application.yml` (ex.: `8081`) ou liberar a porta.

### H2 Console não abre
- **Verificar:** 
  - `spring.h2.console.enabled: true`
  - `spring.h2.console.path: /h2`
  - JDBC URL **exata** do `application.yml`

### `schema.sql`/`data.sql` não executam
- **Verificar:** 
  - `spring.sql.init.mode: always`
  - Arquivos em `src/main/resources`
  - `spring.jpa.hibernate.ddl-auto: none` (evita conflito com Hibernate)

### Swagger UI 404
- **Verificar:** dependência `org.springdoc:springdoc-openapi-starter-webmvc-ui` no `pom.xml`
- Endereço correto: `/swagger-ui/index.html#/` (atenção ao path)

### Erros com MapStruct
- **Sintoma:** classes geradas não aparecem
- **Como resolver:** garantir `mapstruct-processor` como annotation processor no `pom.xml` (ou configurar no IDE), limpar `target/` e `./mvnw clean compile`.

### Datas / `LocalDate` serialização
- **Sintoma:** formatação errada ou falha de desserialização
- **Como resolver:** registrar `JavaTimeModule` no `ObjectMapper` (em geral o Spring Boot já registra), padronizar formato ISO nas trocas (ex.: `"2025-08-20"`).

### 404/400 não padronizado
- **Verificar:** `ApiExceptionHandler` e a assinatura dos métodos (`@ExceptionHandler`, `@ResponseStatus`), além de `@Valid` nos endpoints que recebem body.

### CORS (se houver front-end)
- **Sintoma:** bloqueio de requisições do browser
- **Como resolver:** configurar CORS global no `WebMvcConfigurer` ou com `@CrossOrigin` nos controllers.

---

## 3) Próximos passos recomendados

### Banco & Testes
- **Testcontainers**: substituir H2 por Postgres (ou outro alvo) em testes de integração para maior fidelidade.
- **Migrações**: adotar **Flyway** ou **Liquibase** para versionar DDL/DML (no lugar de `schema.sql`/`data.sql` em produção).

### Configuração & Ambientes
- **Profiles**: separar `application-dev.yml`, `application-test.yml`, `application-prod.yml`.  
  - `dev`: H2, logs verbosos, Swagger ON  
  - `test`: bancos efêmeros com Testcontainers  
  - `prod`: banco real, Swagger OFF (ou protegido)
- **Secrets**: externalizar credenciais via variáveis de ambiente e `Spring Boot Config` apropriado.

### Observabilidade
- **Micrometer**: métricas padrão + customizadas (temporizadores de service, contadores por status).  
- **Logs estruturados** (JSON) + correlação (traceId).  
- **Tracing** (OpenTelemetry) para seguir requisições ponta-a-ponta.

### Qualidade & Segurança
- **Validações Bean Validation** nos DTOs (`@NotBlank`, `@Size`, etc.) e `@Valid` no controller.  
- **Spring Security** (básico → JWT/OAuth2), restrição de métodos e escopos.  
- **Static Analysis**: Checkstyle/SpotBugs/PMD; **Mutation Testing**: PIT.

### Empacotamento & Deploy
- **Dockerfile** multi-stage (JLink/JVM 21) + **docker-compose** local
- **CI/CD** (GitHub Actions):
  - build, testes, análise estática, geração do OpenAPI e publicação de artefatos
  - job para build de imagem Docker e push em registry
- **Versionamento de API**: iniciar `v1` e planejar depreciações/contratos

### Performance & UX de API
- **Paginação** consistente (defaults e ordenação), campos para `totalElements`, `page`, `size`
- **Filtros** mais ricos (ex.: ranges, múltiplos valores) e ordenações bem documentadas
- **Idempotência** em `POST` críticos (chave de deduplicação)

---

## 4) Quick scripts úteis

### Rodar app
```bash
./mvnw spring-boot:run
```

### Rodar testes
```bash
./mvnw test
```

### Endpoints de verificação rápida
```bash
# Swagger JSON
curl -s http://localhost:8080/v3/api-docs | head

# Lista de ninjas (paginada)
curl -s "http://localhost:8080/v1/ninjas?page=0&size=5"

# Not Found (para validar ProblemDetail)
curl -i http://localhost:8080/v1/ninjas/999
```

---

## 5) Encerramento

Com este checklist validado e os próximos passos definidos, seu projeto está pronto para ser usado como **material didático** e para evoluir rumo a ambientes **reais** (testes de integração com Testcontainers, perfis de execução, observabilidade e pipeline CI/CD).
