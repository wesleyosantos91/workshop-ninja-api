# README_STEP_6 — Controller (/v1/ninjas)

## Objetivo
Expor e validar os **endpoints REST** do recurso Ninja no path base **`/v1/ninjas`**, com exemplos `curl/httpie` e testes `MockMvc`.

---

## 1) Arquivo real do projeto
- **Controller:** `src/main/java/br/org/soujava/bsb/api/api/v1/controller/NinjaController.java` — [abrir](sandbox:/mnt/data/step6_refs_NinjaController.java)

Anotações de classe (recorte):
```java
@RestController
@RequestMapping("/v1/ninjas")
public class NinjaController { ... }
```

---

## 2) Endpoints expostos (visão geral)

1. **POST `/v1/ninjas`** — cria um ninja  
   **Request Body:** `NinjaRequest` (JSON em snake_case)  
   **Response:** `201 Created` + `Location` com `/v1/ninjas/{id}` e corpo `NinjaResponse`

2. **GET `/v1/ninjas`** — busca paginada/filtrada  
   **Query Params:** filtros de `NinjaQueryRequest` + paginação (`page`, `size`, `sort`)  
   **Response:** `200 OK` com `PagedModel<NinjaResponse>`

3. **GET `/v1/ninjas/{id}`** — busca por ID  
   **Response:** `200 OK` com `NinjaResponse` ou `404 Not Found`

4. **PUT `/v1/ninjas/{id}`** — atualiza por ID  
   **Request Body:** `NinjaRequest`  
   **Response:** `200 OK` com `NinjaResponse` ou `404 Not Found`

5. **DELETE `/v1/ninjas/{id}`** — remove por ID  
   **Response:** `204 No Content` ou `404 Not Found`

> O controller utiliza `NinjaService` para as operações e `NinjaMapper` para conversão entre DTOs e entidade.

---

## 3) Exemplos de uso (httpie/curl)

### 3.1 Criar
```bash
http POST :8080/v1/ninjas   nome="Naruto Uzumaki" vila="Konoha" cla="Uzumaki" rank="Genin"   chakra_tipo="Vento" especialidade="Ninjutsu" status="Ativo" nivel_forca:=85   data_registro="2025-08-20"
# curl equivalente:
curl -X POST http://localhost:8080/v1/ninjas -H "Content-Type: application/json" -d '{
  "nome":"Naruto Uzumaki","vila":"Konoha","cla":"Uzumaki","rank":"Genin",
  "chakra_tipo":"Vento","especialidade":"Ninjutsu","status":"Ativo",
  "nivel_forca":85,"data_registro":"2025-08-20"
}'
```

### 3.2 Buscar por página
```bash
http GET :8080/v1/ninjas page==0 size==10 nome==nar vila==konoha sort=="nome,asc"
# curl:
curl "http://localhost:8080/v1/ninjas?page=0&size=10&nome=nar&vila=konoha&sort=nome,asc"
```

### 3.3 Buscar por ID
```bash
http GET :8080/v1/ninjas/1
curl -X GET http://localhost:8080/v1/ninjas/1
```

### 3.4 Atualizar
```bash
http PUT :8080/v1/ninjas/1   nome="Naruto Uzumaki" vila="Konoha" cla="Uzumaki" rank="Chunin"   chakra_tipo="Vento" especialidade="Ninjutsu" status="Ativo" nivel_forca:=90   data_registro="2025-08-20"
# curl:
curl -X PUT http://localhost:8080/v1/ninjas/1 -H "Content-Type: application/json" -d '{
  "nome":"Naruto Uzumaki","vila":"Konoha","cla":"Uzumaki","rank":"Chunin",
  "chakra_tipo":"Vento","especialidade":"Ninjutsu","status":"Ativo",
  "nivel_forca":90,"data_registro":"2025-08-20"
}'
```

### 3.5 Remover
```bash
http DELETE :8080/v1/ninjas/1
curl -X DELETE http://localhost:8080/v1/ninjas/1
```

---

## 4) Testes de Controller com MockMvc

Crie `src/test/java/.../api/v1/controller/NinjaControllerTest.java`:

```java
package br.org.soujava.bsb.api.api.v1.controller;

import br.org.soujava.bsb.api.api.v1.request.NinjaRequest;
import br.org.soujava.bsb.api.api.v1.response.NinjaResponse;
import br.org.soujava.bsb.api.domain.entity.NinjaEntity;
import br.org.soujava.bsb.api.domain.exception.ResourceNotFoundException;
import br.org.soujava.bsb.api.domain.service.NinjaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NinjaController.class)
class NinjaControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private NinjaService service;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void postDeveCriarERetornar201() throws Exception {
        NinjaEntity salvo = new NinjaEntity();
        salvo.setId(1);
        salvo.setNome("Naruto Uzumaki");

        given(service.create(any(NinjaRequest.class))).willReturn(salvo);

        NinjaRequest req = new NinjaRequest(
            "Naruto Uzumaki","Konoha","Uzumaki","Genin",
            "Vento","Ninjutsu",null,"Ativo",85, LocalDate.parse("2025-08-20")
        );

        mvc.perform(post("/v1/ninjas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/v1/ninjas/1"));
    }

    @Test
    void getPorId_deveRetornar200() throws Exception {
        NinjaEntity entity = new NinjaEntity();
        entity.setId(1);
        entity.setNome("Naruto Uzumaki");

        given(service.findById(1)).willReturn(entity);

        mvc.perform(get("/v1/ninjas/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.nome").value("Naruto Uzumaki"));
    }

    @Test
    void getPorId_quandoNaoExiste_404() throws Exception {
        given(service.findById(999)).willThrow(new ResourceNotFoundException("Not found"));

        mvc.perform(get("/v1/ninjas/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void putDeveAtualizarERetornar200() throws Exception {
        NinjaEntity atualizado = new NinjaEntity();
        atualizado.setId(1);
        atualizado.setNome("Naruto Uzumaki");
        atualizado.setRank("Chunin");

        given(service.update(eq(1), any(NinjaRequest.class))).willReturn(atualizado);

        NinjaRequest req = new NinjaRequest(
            "Naruto Uzumaki","Konoha","Uzumaki","Chunin",
            "Vento","Ninjutsu",null,"Ativo",90, LocalDate.parse("2025-08-20")
        );

        mvc.perform(put("/v1/ninjas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.rank").value("Chunin"));
    }

    @Test
    void deleteDeveRetornar204() throws Exception {
        doNothing().when(service).delete(1);

        mvc.perform(delete("/v1/ninjas/1"))
            .andExpect(status().isNoContent());
    }
}
```

> **Dicas**:
> - O controller usa o `NinjaMapper` internamente para resposta. Nos testes, validamos apenas o **contrato HTTP** (status, headers, JSON) simulando o retorno do **service**.
> - Se houver `@Valid` no request, adicione testes 400 (payload inválido).

---

## 5) Checklist do STEP 6
- [ ] Todos os endpoints expostos e documentados
- [ ] Exemplos `curl/httpie` funcionam
- [ ] Testes `MockMvc` cobrem 201/200/404/204 e validações

---

## 6) Próximo passo
Ir para **[STEP 7 — Tratamento de Erros](README_STEP_7.md)** para padronizar o payload de erro com `ApiExceptionHandler` e criar testes para 400/404.
