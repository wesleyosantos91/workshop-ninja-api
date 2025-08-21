# README_STEP_4 — DTOs (request/query/response) + Mapper

## Objetivo
Explicar os **DTOs** usados na API e como eles trafegam no **Controller**, garantindo **payloads em snake_case** e mapeamento com **MapStruct**.

---

## 1) Arquivos reais do projeto
- **Request:** `api/v1/request/NinjaRequest.java` — [abrir](sandbox:/mnt/data/step4_refs/NinjaRequest.java)
- **Query Request:** `api/v1/request/NinjaQueryRequest.java` — [abrir](sandbox:/mnt/data/step4_refs/NinjaQueryRequest.java)
- **Response:** `api/v1/response/NinjaResponse.java` — [abrir](sandbox:/mnt/data/step4_refs/NinjaResponse.java)
- **Mapper:** `core/mapper/NinjaMapper.java` — [abrir](sandbox:/mnt/data/step4_refs/NinjaMapper.java)

---

## 2) Convenções de serialização
Os DTOs usam Jackson com **snake_case** no JSON (anotações `@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)` e `@JsonInclude`).  
Isso significa que campos Java em `camelCase` aparecem como `snake_case` no payload.

---

## 3) Modelos de DTO (visão geral)

### 3.1 NinjaRequest
Usado para **criar/atualizar** um ninja. Campos (vide arquivo real):
- `nome` (String)
- `vila` (String)
- `cla` (String)
- `rank` (String)
- `chakraTipo` (String)
- `especialidade` (String)
- `kekkeiGenkai` (String)
- `status` (String)
- `nivelForca` (Integer)
- `dataRegistro` (LocalDate)

**Exemplo JSON (snake_case):**
```json
{
  "nome": "Naruto Uzumaki",
  "vila": "Konoha",
  "cla": "Uzumaki",
  "rank": "Genin",
  "chakra_tipo": "Vento",
  "especialidade": "Ninjutsu",
  "kekkei_genkai": null,
  "status": "Ativo",
  "nivel_forca": 85,
  "data_registro": "2025-08-20"
}
```

### 3.2 NinjaQueryRequest
Usado para **filtro/paginação** em consultas (GET /v1/ninjas).  
Campos comuns de filtro (ver arquivo): por nome, vila, cla, rank etc., além de `page`, `size` via `Pageable` no controller.

**Exemplo de chamada com query params:**
```
GET /v1/ninjas?nome=nar&vila=konoha&page=0&size=10
```

### 3.3 NinjaResponse
Retorno padrão para o cliente. Normalmente inclui o `id` e os mesmos atributos de exibição:

**Exemplo JSON (snake_case):**
```json
{
  "id": 1,
  "nome": "Naruto Uzumaki",
  "vila": "Konoha",
  "cla": "Uzumaki",
  "rank": "Genin",
  "chakra_tipo": "Vento",
  "especialidade": "Ninjutsu",
  "kekkei_genkai": null,
  "status": "Ativo",
  "nivel_forca": 85,
  "data_registro": "2025-08-20"
}
```

---

## 4) Mapper (MapStruct)

Trecho (vide arquivo completo):
```java
@Mapper(componentModel = "spring")
public interface NinjaMapper {

    NinjaMapper MAPPER = Mappers.getMapper(NinjaMapper.class);

    NinjaEntity toEntity(NinjaRequest request);

    NinjaResponse toResponse(NinjaEntity entity);

    default List<NinjaResponse> toListResponse(List<NinjaEntity> entities) {
        final List<NinjaResponse> list = new ArrayList<>();
        entities.forEach(e -> list.add(toResponse(e)));
        return list;
    }

    default Page<NinjaResponse> toPageResponse(Page<NinjaEntity> pages) {
        final List<NinjaResponse> list = toListResponse(pages.getContent());
        return new PageImpl<>(list, pages.getPageable(), pages.getTotalElements());
    }
}
```

> O Mapper converte **request → entity** e **entity → response**, além de **listas** e **páginas**.  
> Caso precise ajustar nomes de campos diferentes entre DTO e Entity, adicione `@Mapping(source="...", target="...")` nos métodos.

---

## 5) Fluxo no Controller
- **POST /v1/ninjas** recebe `NinjaRequest` (JSON) → `NinjaService.create()` → retorna `NinjaResponse` (201 + Location).
- **GET /v1/ninjas/{id}** retorna `NinjaResponse` (200) ou 404.
- **PUT /v1/ninjas/{id}** recebe `NinjaRequest` → update → `NinjaResponse` (200).
- **GET /v1/ninjas** aceita filtros de `NinjaQueryRequest` (ou `Pageable`) e retorna página de `NinjaResponse` (200).

---

## 6) Testes sugeridos (DTO/serialização)
- **Serialização**: montar um `NinjaRequest` e verificar JSON em **snake_case** (com Jackson `ObjectMapper`).
- **Deserialização**: enviar JSON acima e verificar mapeamento correto para o record `NinjaRequest`.
- **Mapper**: testar `toEntity` e `toResponse` com um objeto completo.

Exemplo (teste de serialização):
```java
@Test
void deveSerializarRequestEmSnakeCase() throws Exception {
    ObjectMapper mapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    NinjaRequest req = new NinjaRequest(
        "Naruto Uzumaki", "Konoha", "Uzumaki", "Genin",
        "Vento", "Ninjutsu", null, "Ativo", 85, LocalDate.parse("2025-08-20")
    );

    String json = mapper.writeValueAsString(req);
    assertThat(json).contains("chakra_tipo");
    assertThat(json).contains("nivel_forca");
    assertThat(json).contains("data_registro");
}
```

---

## 7) Checklist do STEP 4
- [ ] DTOs (`NinjaRequest`, `NinjaQueryRequest`, `NinjaResponse`) **existem** e estão com `@JsonNaming(SnakeCase)`
- [ ] Mapper (`NinjaMapper`) converte entre DTO ↔ Entity e lista/página
- [ ] Exemplos JSON documentados e testados
- [ ] Controller aceita/retorna os DTOs previstos

---

## 8) Próximo passo
Ir para **[STEP 5 — Service](README_STEP_5.md)**: `NinjaService` com `create`, `find`, `update`, `delete`, `search` e testes com Mockito.
