# README_STEP_5 — Service (create, find, update, delete, search)

## Objetivo
Documentar e testar a **camada de serviço** responsável pela regra de negócio da entidade Ninja: `create`, `findById`, `update`, `delete` e `search` — **exatamente como implementado no projeto**.

---

## 1) Arquivo real do projeto
- **Service:** `src/main/java/br/org/soujava/bsb/api/domain/service/NinjaService.java`

Métodos reais (recortes):
```java
public NinjaService(NinjaRepository respository) {
    this.respository = respository;
}

public NinjaEntity create(NinjaRequest ninjaRequest) {
    return respository.save(MAPPER.toEntity(ninjaRequest));
}

public NinjaEntity findById(Integer id) throws ResourceNotFoundException {
    return respository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(format("Not found regitstry with code {0}", id)));
}

public Page<NinjaEntity> search(NinjaQueryRequest queryRequest, Pageable pageable) {
    final var ninjaEntityExample = Example.of(MAPPER.toEntity(queryRequest));
    return respository.findAll(ninjaEntityExample, pageable);
}

public NinjaEntity update(Integer id, NinjaRequest request) throws ResourceNotFoundException {
    final var ninja = MAPPER.toEntity(request, findById(id));
    return respository.save(ninja);
}

public void delete(Integer id) throws ResourceNotFoundException {
    final var ninjaEntity = findById(id);
    respository.delete(ninjaEntity);
}
```
> Observações:
> - O service utiliza o **`NinjaMapper`** (`MAPPER`) para converter entre DTOs e entidade.
> - `findById`, `update`, `delete` lançam `ResourceNotFoundException` quando o ID não existe.
> - `search` usa `Example.of(...)` com um **NinjaEntity** gerado a partir do `NinjaQueryRequest` (consulta por exemplo).

---

## 2) Casos de uso e regras básicas
- **create**: recebe `NinjaRequest`, converte para `NinjaEntity` e persiste (`save`).  
- **findById**: consulta por ID; se não encontrar, lança `ResourceNotFoundException`.  
- **update**: carrega o ninja pelo ID (ou erro 404), aplica campos de `NinjaRequest` via mapper e persiste.  
- **delete**: verifica existência e remove.  
- **search**: aplica filtros de `NinjaQueryRequest` via `Example` + `Pageable`.

> Regras adicionais como **duplicidade**, **validações avançadas** e **autorização** podem ser introduzidas conforme evolução.

---

## 3) Testes unitários (Mockito + JUnit 5)

Crie `src/test/java/.../domain/service/NinjaServiceTest.java`:

```java
package br.org.soujava.bsb.api.domain.service;

import br.org.soujava.bsb.api.api.v1.request.NinjaQueryRequest;
import br.org.soujava.bsb.api.api.v1.request.NinjaRequest;
import br.org.soujava.bsb.api.domain.entity.NinjaEntity;
import br.org.soujava.bsb.api.domain.exception.ResourceNotFoundException;
import br.org.soujava.bsb.api.domain.repository.NinjaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NinjaServiceTest {

    @Mock
    private NinjaRepository repository;

    @InjectMocks
    private NinjaService service;

    private NinjaEntity sample;

    @BeforeEach
    void setUp() {
        sample = new NinjaEntity();
        sample.setId(1);
        sample.setNome("Naruto Uzumaki");
        sample.setVila("Konoha");
        sample.setCla("Uzumaki");
        sample.setRank("Genin");
        sample.setChakraTipo("Vento");
        sample.setEspecialidade("Ninjutsu");
        sample.setKekkeiGenkai(null);
        sample.setStatus("Ativo");
        sample.setNivelForca(85);
        sample.setDataRegistro(LocalDate.parse("2025-08-20"));
    }

    @Test
    void create_deveSalvarEretornarEntidade() {
        // Arrange
        NinjaRequest req = new NinjaRequest(
            "Naruto Uzumaki","Konoha","Uzumaki","Genin",
            "Vento","Ninjutsu",null,"Ativo",85, LocalDate.parse("2025-08-20")
        );
        when(repository.save(any(NinjaEntity.class))).thenAnswer(inv -> {
            NinjaEntity e = inv.getArgument(0);
            e.setId(1);
            return e;
        });

        // Act
        NinjaEntity out = service.create(req);

        // Assert
        assertThat(out.getId()).isNotNull();
        assertThat(out.getNome()).isEqualTo("Naruto Uzumaki");
        verify(repository, times(1)).save(any(NinjaEntity.class));
    }

    @Test
    void findById_quandoExiste_retorna() throws Exception {
        when(repository.findById(1)).thenReturn(Optional.of(sample));

        NinjaEntity out = service.findById(1);

        assertThat(out.getNome()).isEqualTo("Naruto Uzumaki");
        verify(repository).findById(1);
    }

    @Test
    void findById_quandoNaoExiste_lancaNotFound() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_quandoExiste_atualizaCampos() throws Exception {
        when(repository.findById(1)).thenReturn(Optional.of(sample));
        when(repository.save(any(NinjaEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        NinjaRequest req = new NinjaRequest(
            "Naruto Uzumaki","Konoha","Uzumaki","Chunin",
            "Vento","Ninjutsu",null,"Ativo",90, LocalDate.parse("2025-08-20")
        );

        NinjaEntity atualizado = service.update(1, req);

        assertThat(atualizado.getRank()).isEqualTo("Chunin");
        assertThat(atualizado.getNivelForca()).isEqualTo(90);
        verify(repository).save(any(NinjaEntity.class));
    }

    @Test
    void delete_quandoExiste_remove() throws Exception {
        when(repository.findById(1)).thenReturn(Optional.of(sample));

        service.delete(1);

        verify(repository).delete(any(NinjaEntity.class));
    }

    @Test
    void search_retornaPaginaComFiltros() {
        NinjaQueryRequest query = new NinjaQueryRequest("nar", null, null, null, null, null, null, null, null, null);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nome").ascending());
        Page<NinjaEntity> page = new PageImpl<>(List.of(sample), pageable, 1);

        when(repository.findAll(any(Example.class), any(Pageable.class))).thenReturn(page);

        Page<NinjaEntity> result = service.search(query, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getNome()).isEqualTo("Naruto Uzumaki");
        verify(repository).findAll(any(Example.class), any(Pageable.class));
    }
}
```

> **Mock do Mapper?**  
> O service usa o `MAPPER` estático do MapStruct. Nos testes acima, não precisamos mockar o mapper porque checamos apenas interações com o repositório e os efeitos finais; entretanto, se quiser isolar o mapper, considere extrair a dependência para injeção ou usar um `@Spy` com implementação gerada.

---

## 4) Como rodar os testes
```bash
./mvnw test
```

---

## 5) Checklist do STEP 5
- [ ] Cobertura de testes para: `create`, `findById` (sucesso/404), `update`, `delete`, `search`
- [ ] Verificação de interações com o `NinjaRepository`
- [ ] Mensagem de `ResourceNotFoundException` adequada (pode ser validada também)

---

## 6) Próximo passo
Ir para **[STEP 6 — Controller](README_STEP_6.md)**: endpoints em `/v1/ninjas`, exemplos de chamadas `curl/httpie` e testes com `MockMvc`.
