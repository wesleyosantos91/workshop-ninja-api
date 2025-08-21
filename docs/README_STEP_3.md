# Passo 3 - Entity e Repository (Mapeamento Java ↔ Banco)

## O que vamos fazer
Vamos criar a classe `NinjaEntity` que representa um ninja em Java e conectá-la com a tabela do banco de dados.

## 1) Criando a NinjaEntity

A **Entity** é uma classe Java que representa uma tabela do banco de dados.

Crie o arquivo `src/main/java/br/org/soujava/bsb/api/domain/entity/NinjaEntity.java`:

```java
package br.org.soujava.bsb.api.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "NINJA")
public class NinjaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_NINJA")
    private Integer id;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Column(name = "VILA", nullable = false, length = 50)
    private String vila;

    @Column(name = "CLA", length = 50)
    private String cla;

    @Column(name = "RANK", nullable = false, length = 20)
    private String rank;

    @Column(name = "CHAKRA_TIPO", nullable = false, length = 30)
    private String chakraTipo;

    @Column(name = "ESPECIALIDADE", length = 50)
    private String especialidade;

    @Column(name = "KEKKEI_GENKAI", length = 50)
    private String kekkeiGenkai;

    @Column(name = "STATUS", length = 20)
    private String status;

    @Column(name = "NIVEL_FORCA")
    private Integer nivelForca;

    @Column(name = "DATA_REGISTRO")
    private LocalDate dataRegistro;

    // Construtor vazio (obrigatório para JPA)
    public NinjaEntity() {}

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getVila() { return vila; }
    public void setVila(String vila) { this.vila = vila; }

    public String getCla() { return cla; }
    public void setCla(String cla) { this.cla = cla; }

    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }

    public String getChakraTipo() { return chakraTipo; }
    public void setChakraTipo(String chakraTipo) { this.chakraTipo = chakraTipo; }

    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }

    public String getKekkeiGenkai() { return kekkeiGenkai; }
    public void setKekkeiGenkai(String kekkeiGenkai) { this.kekkeiGenkai = kekkeiGenkai; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getNivelForca() { return nivelForca; }
    public void setNivelForca(Integer nivelForca) { this.nivelForca = nivelForca; }

    public LocalDate getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDate dataRegistro) { this.dataRegistro = dataRegistro; }
}
```

**Entendendo as anotações:**
- `@Entity` - marca a classe como uma tabela do banco
- `@Table(name = "NINJA")` - nome da tabela no banco
- `@Id` - marca o campo como chave primária
- `@GeneratedValue` - valor gerado automaticamente (AUTO_INCREMENT)
- `@Column` - mapeia campo Java para coluna do banco

## 2) Criando o Repository

O **Repository** é uma interface que nos permite fazer operações no banco (buscar, salvar, deletar).

Crie o arquivo `src/main/java/br/org/soujava/bsb/api/domain/repository/NinjaRepository.java`:

```java
package br.org.soujava.bsb.api.domain.repository;

import br.org.soujava.bsb.api.domain.entity.NinjaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NinjaRepository extends JpaRepository<NinjaEntity, Integer> {
    
    // Métodos automáticos já disponíveis:
    // - findAll() - busca todos os ninjas
    // - findById(id) - busca por ID
    // - save(ninja) - salva ou atualiza
    // - deleteById(id) - deleta por ID
    
    // Você pode criar métodos personalizados aqui se precisar
}
```

**O que o Repository nos dá:**
- `findAll()` - busca todos os ninjas
- `findById(id)` - busca ninja por ID
- `save(ninja)` - salva ou atualiza um ninja
- `deleteById(id)` - deleta ninja por ID
- E muito mais automaticamente!

## 3) Testando se funcionou com NinjaRepositoryTest

Vamos criar testes para validar se nossa Entity e Repository estão funcionando corretamente. Os testes são fundamentais para garantir que nosso código funciona como esperado.

### 3.1) Por que testar?

**Testes automatizados nos ajudam a:**
- ✅ Verificar se o código funciona corretamente
- ✅ Detectar problemas antes de colocar em produção
- ✅ Ter confiança para fazer mudanças
- ✅ Documentar como o código deve funcionar

### 3.2) Criando a classe de teste

Crie o arquivo `src/test/java/br/org/soujava/bsb/api/domain/repository/NinjaRepositoryTest.java`:

```java
package br.org.soujava.bsb.api.domain.repository;

import br.org.soujava.bsb.api.domain.entity.NinjaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Ninja Repository")
class NinjaRepositoryTest {

    @Autowired
    private NinjaRepository ninjaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Deve carregar os dados iniciais do data.sql")
    void deveCarregarDadosIniciais() {
        // Given: dados já carregados pelo data.sql

        // When: buscar todos os ninjas
        List<NinjaEntity> ninjas = ninjaRepository.findAll();

        // Then: deve ter os 4 ninjas iniciais
        assertThat(ninjas).hasSize(4);
        assertThat(ninjas)
            .extracting(NinjaEntity::getNome)
            .containsExactlyInAnyOrder(
                "Naruto Uzumaki",
                "Sasuke Uchiha", 
                "Sakura Haruno",
                "Gaara"
            );
    }

    @Test
    @DisplayName("Deve buscar ninja por ID existente")
    void deveBuscarNinjaPorIdExistente() {
        // Given: ID de um ninja existente (Naruto é geralmente ID 1)
        Integer idNaruto = 1;

        // When: buscar por ID
        Optional<NinjaEntity> ninjaEncontrado = ninjaRepository.findById(idNaruto);

        // Then: deve encontrar o ninja
        assertThat(ninjaEncontrado).isPresent();
        assertThat(ninjaEncontrado.get().getNome()).isEqualTo("Naruto Uzumaki");
        assertThat(ninjaEncontrado.get().getVila()).isEqualTo("Konoha");
        assertThat(ninjaEncontrado.get().getRank()).isEqualTo("Hokage");
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar ninja por ID inexistente")
    void deveRetornarVazioParaIdInexistente() {
        // Given: ID que não existe
        Integer idInexistente = 999;

        // When: buscar por ID inexistente
        Optional<NinjaEntity> ninja = ninjaRepository.findById(idInexistente);

        // Then: deve retornar Optional vazio
        assertThat(ninja).isEmpty();
    }

    @Test
    @DisplayName("Deve salvar novo ninja com sucesso")
    void deveSalvarNovoNinja() {
        // Given: novo ninja para salvar
        NinjaEntity novoNinja = new NinjaEntity();
        novoNinja.setNome("Kakashi Hatake");
        novoNinja.setVila("Konoha");
        novoNinja.setCla("Hatake");
        novoNinja.setRank("Jounin");
        novoNinja.setChakraTipo("Raio");
        novoNinja.setEspecialidade("Ninjutsu");
        novoNinja.setKekkeiGenkai("Sharingan");
        novoNinja.setStatus("Ativo");
        novoNinja.setNivelForca(92);
        novoNinja.setDataRegistro(LocalDate.now());

        // When: salvar ninja
        NinjaEntity ninjaSalvo = ninjaRepository.save(novoNinja);

        // Then: deve salvar com ID gerado
        assertThat(ninjaSalvo.getId()).isNotNull();
        assertThat(ninjaSalvo.getNome()).isEqualTo("Kakashi Hatake");

        // Verificar se foi realmente persistido
        Optional<NinjaEntity> ninjaVerificacao = ninjaRepository.findById(ninjaSalvo.getId());
        assertThat(ninjaVerificacao).isPresent();
        assertThat(ninjaVerificacao.get().getNome()).isEqualTo("Kakashi Hatake");
    }
}
```

### 3.3) Entendendo as anotações de teste

**@DataJpaTest**
- Configura um teste focado apenas na camada JPA
- Cria um banco de dados em memória para o teste
- Carrega apenas os componentes relacionados ao JPA

**@ActiveProfiles("test")**  
- Ativa o profile "test" 
- Permite configurações específicas para testes

**@DisplayName("...")**
- Nome amigável para o teste
- Aparece nos relatórios de teste

**@Test**
- Marca o método como um teste
- Será executado pelo framework de testes

**@Autowired**
- Injeta automaticamente as dependências
- Spring cria e injeta o NinjaRepository

### 3.4) Padrão Given-When-Then

Nossos testes seguem o padrão **Given-When-Then**:

```java
// Given: Preparação - o que já existe
Integer idNaruto = 1;

// When: Ação - o que vamos testar  
Optional<NinjaEntity> ninja = ninjaRepository.findById(idNaruto);

// Then: Verificação - o que esperamos
assertThat(ninja).isPresent();
```

**Given** = Contexto (o que já temos)
**When** = Ação (o que fazemos)  
**Then** = Resultado esperado (o que deve acontecer)

### 3.5) Entendendo as asserções (AssertJ)

**assertThat()** - Início de uma verificação
**isPresent()** - Verifica se Optional não está vazio
**isEmpty()** - Verifica se Optional está vazio
**hasSize(4)** - Verifica se lista tem 4 elementos
**isEqualTo("Naruto")** - Verifica se valor é igual
**isNotNull()** - Verifica se não é nulo

### 3.6) Executando os testes

**No terminal:**
```bash
# Executa todos os testes
./mvnw test

# Executa apenas os testes do Repository
./mvnw test -Dtest=NinjaRepositoryTest
```

**Na IDE (IntelliJ/Eclipse):**
- Clique com botão direito na classe de teste
- Selecione "Run NinjaRepositoryTest"
- Ou clique no ícone de "play" ao lado da classe

### 3.7) O que cada teste valida

1. **deveCarregarDadosIniciais()** 
   - Verifica se o data.sql foi carregado
   - Confirma que temos 4 ninjas na base

2. **deveBuscarNinjaPorIdExistente()**
   - Testa se conseguimos buscar um ninja pelo ID
   - Valida se os dados estão corretos

3. **deveRetornarVazioParaIdInexistente()**
   - Testa o comportamento quando ID não existe
   - Deve retornar Optional.empty()

4. **deveSalvarNovoNinja()**
   - Testa se conseguimos salvar um novo ninja
   - Verifica se o ID é gerado automaticamente
   - Confirma que foi persistido no banco

### 3.8) Resultado esperado

Quando você executar os testes, deve ver algo como:
```
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Se algum teste falhar, você verá uma mensagem detalhada explicando o que deu errado.

## Próximo passo
Agora vamos criar os DTOs (objetos para receber/enviar dados pela API) e o Mapper. **[STEP 4 — DTOs](README_STEP_4.md)**
