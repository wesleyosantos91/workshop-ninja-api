# Passo 5 - Service (Lógica de Negócio)

## O que vamos fazer
Vamos criar o `NinjaService` que contém toda a lógica de negócio: criar, buscar, atualizar e deletar ninjas.

## 1) O que é um Service?

O **Service** é onde fica a lógica da sua aplicação:
- Regras de negócio
- Validações
- Operações CRUD (Create, Read, Update, Delete)
- Comunicação com o Repository

**Arquitetura em camadas:**
- **Controller** → recebe requisições HTTP
- **Service** → processa a lógica de negócio  
- **Repository** → acessa o banco de dados

## 2) Criando uma exceção personalizada

Primeiro, vamos criar uma exceção para quando não encontrarmos um ninja.

Crie `src/main/java/br/org/soujava/bsb/api/domain/exception/ResourceNotFoundException.java`:

```java
package br.org.soujava.bsb.api.domain.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

## 3) Criando o NinjaService

Crie `src/main/java/br/org/soujava/bsb/api/domain/service/NinjaService.java`:

```java
package br.org.soujava.bsb.api.domain.service;

import br.org.soujava.bsb.api.api.v1.request.NinjaQueryRequest;
import br.org.soujava.bsb.api.api.v1.request.NinjaRequest;
import br.org.soujava.bsb.api.api.v1.response.NinjaResponse;
import br.org.soujava.bsb.api.core.mapper.NinjaMapper;
import br.org.soujava.bsb.api.domain.entity.NinjaEntity;
import br.org.soujava.bsb.api.domain.exception.ResourceNotFoundException;
import br.org.soujava.bsb.api.domain.repository.NinjaRepository;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NinjaService {

    private final NinjaRepository ninjaRepository;
    private final NinjaMapper ninjaMapper;

    public NinjaService(NinjaRepository ninjaRepository, NinjaMapper ninjaMapper) {
        this.ninjaRepository = ninjaRepository;
        this.ninjaMapper = ninjaMapper;
    }

    // 1) BUSCAR TODOS OS NINJAS
    public List<NinjaResponse> findAll() {
        return ninjaRepository.findAll()
                .stream()
                .map(ninjaMapper::entityToResponse)
                .toList();
    }

    // 2) BUSCAR NINJA POR ID
    public NinjaResponse findById(Integer id) {
        NinjaEntity ninja = ninjaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ninja não encontrado com ID: " + id));
        
        return ninjaMapper.entityToResponse(ninja);
    }

    // 3) BUSCAR COM FILTROS
    public List<NinjaResponse> findByQuery(NinjaQueryRequest query) {
        NinjaEntity example = ninjaMapper.queryToEntity(query);
        Example<NinjaEntity> ninjaExample = Example.of(example);
        
        return ninjaRepository.findAll(ninjaExample)
                .stream()
                .map(ninjaMapper::entityToResponse)
                .toList();
    }

    // 4) CRIAR NOVO NINJA
    public NinjaResponse create(NinjaRequest request) {
        NinjaEntity ninja = ninjaMapper.requestToEntity(request);
        NinjaEntity savedNinja = ninjaRepository.save(ninja);
        
        return ninjaMapper.entityToResponse(savedNinja);
    }

    // 5) ATUALIZAR NINJA EXISTENTE
    public NinjaResponse update(Integer id, NinjaRequest request) {
        // Verifica se o ninja existe
        NinjaEntity existingNinja = ninjaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ninja não encontrado com ID: " + id));
        
        // Converte o request para entity
        NinjaEntity ninja = ninjaMapper.requestToEntity(request);
        ninja.setId(id); // Mantém o ID original
        
        // Salva e retorna
        NinjaEntity updatedNinja = ninjaRepository.save(ninja);
        return ninjaMapper.entityToResponse(updatedNinja);
    }

    // 6) DELETAR NINJA
    public void deleteById(Integer id) {
        // Verifica se o ninja existe antes de deletar
        if (!ninjaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ninja não encontrado com ID: " + id);
        }
        
        ninjaRepository.deleteById(id);
    }
}
```

## 4) Entendendo o código

**Injeção de dependência:**
```java
private final NinjaRepository ninjaRepository;
private final NinjaMapper ninjaMapper;
```
O Spring injeta automaticamente essas dependências.

**Busca com Example:**
```java
Example<NinjaEntity> ninjaExample = Example.of(example);
```
O `Example` permite buscar registros que "parecem" com o objeto exemplo (campos não nulos são usados como filtro).

**Tratamento de erro:**
```java
.orElseThrow(() -> new ResourceNotFoundException("Ninja não encontrado"));
```
Se não encontrar o ninja, lança nossa exceção personalizada.

**Stream para conversão:**
```java
.stream().map(ninjaMapper::entityToResponse).toList()
```
Converte cada `NinjaEntity` em `NinjaResponse`.

## 5) Testando o Service com NinjaServiceTest

Agora vamos criar testes unitários para validar se nosso Service está funcionando corretamente. Testes de Service são diferentes dos testes de Repository - aqui usamos **Mocks** para simular as dependências.

### 5.1) Por que testar o Service?

**Testes de Service validam:**
- ✅ Lógica de negócio funciona corretamente
- ✅ Tratamento de exceções está adequado
- ✅ Interações com Repository acontecem como esperado
- ✅ Conversões entre DTOs e Entities estão corretas

### 5.2) Diferença entre testes de Repository e Service

| **Repository Test** | **Service Test** |
|-------------------|------------------|
| Testa integração com banco | Testa lógica de negócio |
| Usa banco H2 real | Usa mocks das dependências |
| `@DataJpaTest` | `@ExtendWith(MockitoExtension.class)` |
| Carrega contexto JPA | Não carrega contexto Spring |

### 5.3) Criando a classe de teste

Crie o arquivo `src/test/java/br/org/soujava/bsb/api/domain/service/NinjaServiceTest.java`:

```java
package br.org.soujava.bsb.api.domain.service;

import br.org.soujava.bsb.api.api.v1.request.NinjaQueryRequest;
import br.org.soujava.bsb.api.api.v1.request.NinjaRequest;
import br.org.soujava.bsb.api.api.v1.response.NinjaResponse;
import br.org.soujava.bsb.api.core.mapper.NinjaMapper;
import br.org.soujava.bsb.api.domain.entity.NinjaEntity;
import br.org.soujava.bsb.api.domain.exception.ResourceNotFoundException;
import br.org.soujava.bsb.api.domain.repository.NinjaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Ninja Service")
class NinjaServiceTest {

    @Mock
    private NinjaRepository ninjaRepository;

    @Mock
    private NinjaMapper ninjaMapper;

    @InjectMocks
    private NinjaService ninjaService;

    private NinjaEntity ninjaEntity;
    private NinjaRequest ninjaRequest;
    private NinjaResponse ninjaResponse;
    private NinjaQueryRequest ninjaQueryRequest;

    @BeforeEach
    void setUp() {
        // Preparar dados de teste que serão reutilizados
        ninjaEntity = new NinjaEntity();
        ninjaEntity.setId(1);
        ninjaEntity.setNome("Naruto Uzumaki");
        ninjaEntity.setVila("Konoha");
        ninjaEntity.setCla("Uzumaki");
        ninjaEntity.setRank("Hokage");
        ninjaEntity.setChakraTipo("Vento");
        ninjaEntity.setEspecialidade("Ninjutsu");
        ninjaEntity.setKekkeiGenkai("Kurama (Bijuu)");
        ninjaEntity.setStatus("Ativo");
        ninjaEntity.setNivelForca(98);
        ninjaEntity.setDataRegistro(LocalDate.now());

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
                LocalDate.now()
        );

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
                LocalDate.now()
        );

        ninjaQueryRequest = new NinjaQueryRequest(
                "Naruto",
                "Konoha",
                "Hokage",
                "Vento",
                "Ativo"
        );
    }

    @Test
    @DisplayName("Deve criar ninja com sucesso")
    void deveCriarNinjaComSucesso() {
        // Given: configurar comportamento dos mocks
        when(ninjaMapper.requestToEntity(ninjaRequest)).thenReturn(ninjaEntity);
        when(ninjaRepository.save(any(NinjaEntity.class))).thenReturn(ninjaEntity);
        when(ninjaMapper.entityToResponse(ninjaEntity)).thenReturn(ninjaResponse);

        // When: chamar create
        NinjaResponse resultado = ninjaService.create(ninjaRequest);

        // Then: deve retornar ninja criado
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1);
        assertThat(resultado.nome()).isEqualTo("Naruto Uzumaki");
        assertThat(resultado.vila()).isEqualTo("Konoha");

        // Verificar que os mocks foram chamados corretamente
        verify(ninjaMapper, times(1)).requestToEntity(ninjaRequest);
        verify(ninjaRepository, times(1)).save(any(NinjaEntity.class));
        verify(ninjaMapper, times(1)).entityToResponse(ninjaEntity);
    }

    @Test
    @DisplayName("Deve buscar ninja por ID existente com sucesso")
    void deveBuscarNinjaPorIdExistenteComSucesso() {
        // Given: repository retornará o ninja
        when(ninjaRepository.findById(1)).thenReturn(Optional.of(ninjaEntity));
        when(ninjaMapper.entityToResponse(ninjaEntity)).thenReturn(ninjaResponse);

        // When: buscar ninja por ID
        NinjaResponse resultado = ninjaService.findById(1);

        // Then: deve retornar o ninja encontrado
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1);
        assertThat(resultado.nome()).isEqualTo("Naruto Uzumaki");

        // Verificar interações com mocks
        verify(ninjaRepository, times(1)).findById(1);
        verify(ninjaMapper, times(1)).entityToResponse(ninjaEntity);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar ninja por ID inexistente")
    void deveLancarExcecaoAoBuscarNinjaPorIdInexistente() {
        // Given: repository retornará Optional vazio
        when(ninjaRepository.findById(999)).thenReturn(Optional.empty());

        // When/Then: deve lançar ResourceNotFoundException
        assertThatThrownBy(() -> ninjaService.findById(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Ninja não encontrado com ID: 999");

        // Verificar que o repository foi chamado
        verify(ninjaRepository, times(1)).findById(999);
        // Verificar que o mapper NÃO foi chamado (ninja não encontrado)
        verify(ninjaMapper, times(0)).entityToResponse(any());
    }

    @Test
    @DisplayName("Deve buscar todos os ninjas com sucesso")
    void deveBuscarTodosOsNinjasComSucesso() {
        // Given: repository retornará lista de ninjas
        List<NinjaEntity> ninjas = List.of(ninjaEntity);
        when(ninjaRepository.findAll()).thenReturn(ninjas);
        when(ninjaMapper.entityToResponse(ninjaEntity)).thenReturn(ninjaResponse);

        // When: buscar todos
        List<NinjaResponse> resultado = ninjaService.findAll();

        // Then: deve retornar lista com ninjas
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nome()).isEqualTo("Naruto Uzumaki");

        // Verificar interações
        verify(ninjaRepository, times(1)).findAll();
        verify(ninjaMapper, times(1)).entityToResponse(ninjaEntity);
    }

    @Test
    @DisplayName("Deve buscar ninjas com filtros usando Example")
    void deveBuscarNinjasComFiltros() {
        // Given: preparar mocks
        NinjaEntity entityFiltro = new NinjaEntity();
        entityFiltro.setNome("Naruto");
        entityFiltro.setVila("Konoha");
        
        List<NinjaEntity> ninjas = List.of(ninjaEntity);
        
        when(ninjaMapper.queryToEntity(ninjaQueryRequest)).thenReturn(entityFiltro);
        when(ninjaRepository.findAll(any(Example.class))).thenReturn(ninjas);
        when(ninjaMapper.entityToResponse(ninjaEntity)).thenReturn(ninjaResponse);

        // When: fazer busca com filtros
        List<NinjaResponse> resultado = ninjaService.findByQuery(ninjaQueryRequest);

        // Then: deve retornar resultados filtrados
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nome()).isEqualTo("Naruto Uzumaki");

        // Verificar interações
        verify(ninjaMapper, times(1)).queryToEntity(ninjaQueryRequest);
        verify(ninjaRepository, times(1)).findAll(any(Example.class));
        verify(ninjaMapper, times(1)).entityToResponse(ninjaEntity);
    }

    @Test
    @DisplayName("Deve atualizar ninja existente com sucesso")
    void deveAtualizarNinjaExistenteComSucesso() {
        // Given: ninja existe
        when(ninjaRepository.findById(1)).thenReturn(Optional.of(ninjaEntity));
        when(ninjaMapper.requestToEntity(ninjaRequest)).thenReturn(ninjaEntity);
        when(ninjaRepository.save(any(NinjaEntity.class))).thenReturn(ninjaEntity);
        when(ninjaMapper.entityToResponse(ninjaEntity)).thenReturn(ninjaResponse);

        // When: atualizar ninja
        NinjaResponse resultado = ninjaService.update(1, ninjaRequest);

        // Then: deve retornar ninja atualizado
        assertThat(resultado).isNotNull();
        assertThat(resultado.nome()).isEqualTo("Naruto Uzumaki");

        // Verificar interações
        verify(ninjaRepository, times(1)).findById(1);
        verify(ninjaMapper, times(1)).requestToEntity(ninjaRequest);
        verify(ninjaRepository, times(1)).save(any(NinjaEntity.class));
        verify(ninjaMapper, times(1)).entityToResponse(ninjaEntity);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar ninja inexistente")
    void deveLancarExcecaoAoTentarAtualizarNinjaInexistente() {
        // Given: ninja não existe
        when(ninjaRepository.findById(999)).thenReturn(Optional.empty());

        // When/Then: deve lançar exceção
        assertThatThrownBy(() -> ninjaService.update(999, ninjaRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Ninja não encontrado com ID: 999");

        // Verificar que apenas findById foi chamado
        verify(ninjaRepository, times(1)).findById(999);
        verify(ninjaRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Deve deletar ninja existente com sucesso")
    void deveDeletarNinjaExistenteComSucesso() {
        // Given: ninja existe
        when(ninjaRepository.existsById(1)).thenReturn(true);

        // When: deletar ninja
        ninjaService.deleteById(1);

        // Then: deve chamar deleteById no repository
        verify(ninjaRepository, times(1)).existsById(1);
        verify(ninjaRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar ninja inexistente")
    void deveLancarExcecaoAoTentarDeletarNinjaInexistente() {
        // Given: ninja não existe
        when(ninjaRepository.existsById(999)).thenReturn(false);

        // When/Then: deve lançar exceção
        assertThatThrownBy(() -> ninjaService.deleteById(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Ninja não encontrado com ID: 999");

        // Verificar que deleteById NÃO foi chamado
        verify(ninjaRepository, times(1)).existsById(999);
        verify(ninjaRepository, times(0)).deleteById(999);
    }
}
```

### 5.4) Entendendo as anotações de teste

**@ExtendWith(MockitoExtension.class)**
- Habilita o uso do Mockito para criar mocks
- Não carrega o contexto Spring (teste mais rápido)

**@Mock**
- Cria um objeto "falso" (mock) da dependência
- Permite controlar o comportamento do objeto

**@InjectMocks**
- Injeta os mocks automaticamente no objeto testado
- Cria uma instância real do NinjaService com mocks injetados

**@BeforeEach**
- Executa antes de cada teste
- Prepara dados que serão reutilizados

### 5.5) Entendendo o Mockito

**when().thenReturn()** - Define comportamento do mock:
```java
when(ninjaRepository.findById(1)).thenReturn(Optional.of(ninjaEntity));
// Quando chamar findById(1), retorne Optional.of(ninjaEntity)
```

**verify()** - Verifica se método foi chamado:
```java
verify(ninjaRepository, times(1)).findById(1);
// Verifica que findById(1) foi chamado exatamente 1 vez
```

**any()** - Aceita qualquer valor como parâmetro:
```java
verify(ninjaRepository).save(any(NinjaEntity.class));
// Verifica que save foi chamado com qualquer NinjaEntity
```

### 5.6) Padrão AAA (Arrange-Act-Assert)

Nossos testes seguem o padrão AAA:

```java
// Arrange (Given): Preparar mocks e dados
when(ninjaRepository.findById(1)).thenReturn(Optional.of(ninjaEntity));

// Act (When): Executar ação que queremos testar
NinjaResponse resultado = ninjaService.findById(1);

// Assert (Then): Verificar resultados e interações
assertThat(resultado).isNotNull();
verify(ninjaRepository).findById(1);
```

### 5.7) Executando os testes

**No terminal:**
```bash
# Executa todos os testes
./mvnw test

# Executa apenas testes do Service
./mvnw test -Dtest=NinjaServiceTest
```

### 5.8) O que cada teste valida

1. **deveCriarNinjaComSucesso()** - Criação funcionando corretamente
2. **deveBuscarNinjaPorIdExistenteComSucesso()** - Busca por ID válido
3. **deveLancarExcecaoAoBuscarNinjaPorIdInexistente()** - Tratamento de ninja não encontrado
4. **deveBuscarTodosOsNinjasComSucesso()** - Listagem completa
5. **deveBuscarNinjasComFiltros()** - Busca com Example
6. **deveAtualizarNinjaExistenteComSucesso()** - Atualização funcionando
7. **deveLancarExcecaoAoTentarAtualizarNinjaInexistente()** - Atualização com ID inválido
8. **deveDeletarNinjaExistenteComSucesso()** - Exclusão funcionando
9. **deveLancarExcecaoAoTentarDeletarNinjaInexistente()** - Exclusão com ID inválido

### 5.9) Benefícios dos testes com Mock

✅ **Rápidos** - Não acessam banco de dados
✅ **Isolados** - Testam apenas lógica do Service
✅ **Confiáveis** - Controlam exatamente o comportamento das dependências
✅ **Precisos** - Verificam interações específicas

## Próximo passo
Agora vamos criar o Controller, que vai expor os endpoints HTTP da nossa API. **[STEP 6 — Controller](README_STEP_6.md)**
