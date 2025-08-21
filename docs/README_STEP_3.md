# README_STEP_3 — Entidade e Repository (fiel ao projeto)

## Objetivo
Mapear e documentar a **`NinjaEntity`** e a **`NinjaRepository`**, garantindo alinhamento total com o `schema.sql` e preparando a base para testes de repositório com H2.

---

## 1) Arquivos reais do projeto
- **Entidade:** `src/main/java/br/org/soujava/bsb/api/domain/entity/NinjaEntity.java`  
  — [Baixar cópia](sandbox:/mnt/data/step3_refs/NinjaEntity.java)
- **Repository:** `src/main/java/br/org/soujava/bsb/api/domain/repository/NinjaRepository.java`  
  — [Baixar cópia](sandbox:/mnt/data/step3_refs/NinjaRepository.java)

---

## 2) Mapeamento JPA (visão geral)

- **Tabela:** `NINJA`  
- **Chave primária:** `id` (`Integer`) com `@GeneratedValue(strategy = GenerationType.IDENTITY)`  
- **Campos principais (extraídos do código):**
- `id`: `Integer`
- `nome`: `String`
- `vila`: `String`
- `cla`: `String`
- `rank`: `String`
- `chakraTipo`: `String`
- `especialidade`: `String`
- `kekkeiGenkai`: `String`
- `status`: `String`
- `nivelForca`: `Integer`
- `dataRegistro`: `LocalDate`

> **Observação:** o nome físico da tabela é `NINJA` e os nomes de coluna seguem os declarados na entidade (via `@Column`). Verifique o `schema.sql` para confirmar tipos/tamanhos/constraints.

---

## 3) Trechos reais (entidade e repository)

**`NinjaEntity.java` (recorte):**
```java
@Entity
@Table(name = "NINJA")
public class NinjaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    // demais colunas (@Column): nome, vila, cla, rank, chakraTipo, especialidade, 
    // kekkeiGenkai, status, nivelForca, dataRegistro ...
}
```

**`NinjaRepository.java` (recorte):**
```java
public interface NinjaRepository extends JpaRepository<NinjaEntity, Integer> {
}
```

> O repositório usa **métodos padrão** do `JpaRepository` (`save`, `findById`, `findAll`, `deleteById`, etc.).
> Métodos derivados podem ser adicionados no STEP 5/6 conforme necessidade.

---

## 4) Testes de Repositório (H2) — `@DataJpaTest`

Crie uma classe de teste em `src/test/java/.../domain/repository/NinjaRepositoryTest.java` com foco em **operações básicas** que já existem no repo:

### 4.1. Exemplo de teste — salvar e ler por ID
```java
package br.org.soujava.bsb.api.domain.repository;

import br.org.soujava.bsb.api.domain.entity.NinjaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class NinjaRepositoryTest {
    @Autowired
    private NinjaRepository repository;

    @Test
    void deveSalvarELerPorId() {
        NinjaEntity n = new NinjaEntity();
        n.setNome("Naruto Uzumaki");
        n.setVila("Konoha");
        n.setCla("Uzumaki");
        n.setRank("Genin");
        n.setChakraTipo("Vento");
        n.setEspecialidade("Ninjutsu");
        n.setKekkeiGenkai(null);
        n.setStatus("Ativo");
        n.setNivelForca(85);
        n.setDataRegistro(LocalDate.now());

        NinjaEntity salvo = repository.save(n);
        assertThat(salvo.getId()).isNotNull();

        var encontrado = repository.findById(salvo.getId());
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("Naruto Uzumaki");
    }
}
```

### 4.2. Exemplo de teste — `findAll()`
```java
@Test
void deveListarTodos() {
    var todos = repository.findAll();
    assertThat(todos).isNotNull();
}
```

### 4.3. Exemplo de teste — `deleteById()`
```java
@Test
void deveExcluirPorId() {
    NinjaEntity n = new NinjaEntity();
    n.setNome("Sasuke Uchiha");
    n.setVila("Konoha");
    n.setCla("Uchiha");
    n.setRank("Jounin");
    n.setChakraTipo("Fogo");
    n.setEspecialidade("Ninjutsu/Genjutsu");
    n.setKekkeiGenkai("Sharingan");
    n.setStatus("Ativo");
    n.setNivelForca(97);
    n.setDataRegistro(LocalDate.now());

    var salvo = repository.save(n);
    Integer id = salvo.getId();
    repository.deleteById(id);

    assertThat(repository.findById(id)).isNotPresent();
}
```

> **Banco de teste**: o Spring Boot auto-configura H2 para `@DataJpaTest`. Se necessário, use `@AutoConfigureTestDatabase(replace = Replace.ANY)`.

---

## 5) Checklist do STEP 3
- [ ] Entidade `NinjaEntity` mapeada para `NINJA` e alinhada ao `schema.sql`
- [ ] Repositório `NinjaRepository` estende `JpaRepository<NinjaEntity, Integer>`
- [ ] Testes `@DataJpaTest` **verdes**: salvar, buscar por ID, listar, excluir

---

## 6) Erros comuns & soluções
- **Tipos divergentes** (`schema.sql` vs entidade): padronize tipos e tamanhos (`VARCHAR`, `DATE/TIMESTAMP`, etc.).
- **`GenerationType` incompatível** com DDL: ajuste a estratégia (ex.: `IDENTITY`) para bater com a coluna `ID`.
- **Nomes de coluna**: se usar nomes diferentes no banco (ex.: `NOME`/`VILA` maiúsculos), configure `@Column(name = "...")` para mapear corretamente.
- **Nullability**: anote `@Column(nullable = false)` e/ou use validações em DTOs (STEP 4).

---

## 7) Próximo passo
Avançar para **[STEP 4 — DTOs](README_STEP_4.md)**: `NinjaRequest`, `NinjaQueryRequest`, `NinjaResponse`, exemplos de JSON (snake_case) e integração com o Controller.
