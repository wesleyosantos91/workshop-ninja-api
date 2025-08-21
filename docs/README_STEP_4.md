# Passo 4 - DTOs e Mapper (Objetos para API)

## O que vamos fazer
Vamos criar os DTOs (objetos que trafegam na API), adicionar o MapStruct ao projeto e criar o Mapper (converte automaticamente entre diferentes objetos).

## 1) O que são DTOs?

**DTO** = Data Transfer Object (Objeto de Transferência de Dados)

- São objetos simples que carregam dados entre camadas da aplicação
- **Request**: dados que chegam na API (quando alguém envia dados)
- **Response**: dados que saem da API (quando retornamos dados)
- **Query**: filtros de busca

**Por que usar DTOs?**
- Separar o que vai na API do que fica no banco
- Controlar exatamente quais campos aparecem
- Validar dados de entrada
- Evoluir a API sem afetar o banco de dados

## 2) Adicionando dependências do MapStruct

Antes de criar nossos DTOs e Mapper, precisamos adicionar as dependências do MapStruct no `pom.xml`.

### 2.1) Por que usar MapStruct?

**Sem MapStruct (conversão manual):**
```java
// Muito código repetitivo e propenso a erros
public NinjaResponse toResponse(NinjaEntity entity) {
    NinjaResponse response = new NinjaResponse();
    response.setId(entity.getId());
    response.setNome(entity.getNome());
    response.setVila(entity.getVila());
    response.setCla(entity.getCla());
    response.setRank(entity.getRank());
    // ... mais 6 campos para copiar manualmente
    return response;
}
```

**Com MapStruct (automático):**
```java
// Uma linha só! MapStruct gera todo código automaticamente
NinjaResponse response = ninjaMapper.entityToResponse(entity);
```

**Vantagens do MapStruct:**
- ✅ **Automático** - Gera código de conversão na compilação
- ✅ **Rápido** - Mais rápido que reflexão
- ✅ **Seguro** - Detecta erros em tempo de compilação
- ✅ **Limpo** - Menos código para manter

### 2.2) Adicionando no pom.xml

Adicione essas dependências na seção `<dependencies>` do seu `pom.xml`:

```xml
<!-- MapStruct -->
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.6.3</version>
</dependency>

<!-- MapStruct Processor (para gerar código automaticamente) -->
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct-processor</artifactId>
    <version>1.6.3</version>
    <scope>provided</scope>
</dependency>
```

**Importante:** O `mapstruct-processor` é fundamental! Ele gera automaticamente as classes de implementação durante a compilação.

## 3) Criando os DTOs

### 3.1) NinjaRequest (para receber dados)

Crie `src/main/java/br/org/soujava/bsb/api/api/v1/request/NinjaRequest.java`:

```java
package br.org.soujava.bsb.api.api.v1.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record NinjaRequest(
    String nome,
    String vila,
    String cla,
    String rank,
    String chakraTipo,
    String especialidade,
    String kekkeiGenkai,
    String status,
    Integer nivelForca,
    LocalDate dataRegistro
) {}
```

**Explicando as anotações:**
- `@JsonInclude(NON_NULL)` - só inclui campos que não são nulos
- `@JsonNaming(SnakeCaseStrategy.class)` - converte `chakraTipo` para `chakra_tipo` no JSON

### 3.2) NinjaResponse (para enviar dados)

Crie `src/main/java/br/org/soujava/bsb/api/api/v1/response/NinjaResponse.java`:

```java
package br.org.soujava.bsb.api.api.v1.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record NinjaResponse(
    Integer id,
    String nome,
    String vila,
    String cla,
    String rank,
    String chakraTipo,
    String especialidade,
    String kekkeiGenkai,
    String status,
    Integer nivelForca,
    LocalDate dataRegistro
) {}
```

### 3.3) NinjaQueryRequest (para filtros)

Crie `src/main/java/br/org/soujava/bsb/api/api/v1/request/NinjaQueryRequest.java`:

```java
package br.org.soujava.bsb.api.api.v1.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record NinjaQueryRequest(
    String nome,
    String vila,
    String rank,
    String chakraTipo,
    String status
) {}
```

## 4) Criando o Mapper

O **Mapper** converte automaticamente entre objetos diferentes. Usamos o MapStruct para isso.

### 4.1) Por que usar o padrão Mapper?

**Separação de responsabilidades:**
- **Entity** - Representa dados no banco
- **DTO** - Representa dados na API
- **Mapper** - Faz a ponte entre Entity e DTO

**Benefícios do padrão:**
- ✅ **Flexibilidade** - API pode evoluir independente do banco
- ✅ **Segurança** - Controla exatamente quais dados expor
- ✅ **Manutenibilidade** - Mudanças ficam centralizadas
- ✅ **Testabilidade** - Fácil de testar cada camada

### 4.2) Implementando o NinjaMapper

Crie `src/main/java/br/org/soujava/bsb/api/core/mapper/NinjaMapper.java`:

```java
package br.org.soujava.bsb.api.core.mapper;

import br.org.soujava.bsb.api.api.v1.request.NinjaQueryRequest;
import br.org.soujava.bsb.api.api.v1.request.NinjaRequest;
import br.org.soujava.bsb.api.api.v1.response.NinjaResponse;
import br.org.soujava.bsb.api.domain.entity.NinjaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NinjaMapper {

    NinjaMapper INSTANCE = Mappers.getMapper(NinjaMapper.class);

    // Converte Request para Entity (para salvar no banco)
    NinjaEntity requestToEntity(NinjaRequest request);

    // Converte Entity para Response (para retornar na API)
    NinjaResponse entityToResponse(NinjaEntity entity);

    // Converte Query para Entity (para buscar no banco)
    NinjaEntity queryToEntity(NinjaQueryRequest query);
}
```

**Como funciona o Mapper:**
- **@Mapper** - Marca a interface para o MapStruct processar
- **INSTANCE** - Forma padrão de obter a implementação gerada
- **Métodos de conversão** - MapStruct gera automaticamente baseado nos nomes dos campos

### 4.3) Como o MapStruct funciona "por baixo dos panos"

Quando você compila o projeto, o MapStruct gera automaticamente uma classe `NinjaMapperImpl` como esta:

```java
// Gerado automaticamente - você não precisa escrever isso!
@Generated("org.mapstruct.ap.MappingProcessor")
public class NinjaMapperImpl implements NinjaMapper {

    @Override
    public NinjaEntity requestToEntity(NinjaRequest request) {
        if (request == null) return null;
        
        NinjaEntity entity = new NinjaEntity();
        entity.setNome(request.nome());
        entity.setVila(request.vila());
        entity.setCla(request.cla());
        // ... todos os outros campos
        return entity;
    }
    
    // ... outros métodos de conversão
}
```

## 5) Compilando para gerar o Mapper

Execute no terminal para gerar as implementações:
```bash
# Compila e gera as classes do MapStruct
./mvnw compile
```

**O que acontece:**
1. MapStruct analisa sua interface `NinjaMapper`
2. Gera automaticamente a classe `NinjaMapperImpl`
3. A classe fica disponível em `target/generated-sources/annotations/`

**Verificando se funcionou:**
- Olhe em `target/generated-sources/annotations/` 
- Deve ter a classe `NinjaMapperImpl` gerada automaticamente

## 6) Testando o Mapper (opcional)

Você pode criar um teste simples para verificar se o Mapper funciona:

```java
@Test
void deveConverterEntityParaResponse() {
    // Given: uma entity
    NinjaEntity entity = new NinjaEntity();
    entity.setId(1);
    entity.setNome("Naruto");
    entity.setVila("Konoha");
    
    // When: converter para response
    NinjaResponse response = NinjaMapper.INSTANCE.entityToResponse(entity);
    
    // Then: deve ter os dados corretos
    assertThat(response.id()).isEqualTo(1);
    assertThat(response.nome()).isEqualTo("Naruto");
    assertThat(response.vila()).isEqualTo("Konoha");
}
```

## Próximo passo
Agora vamos criar o Service, que contém a lógica de negócio da nossa aplicação. **[STEP 5 — Service](README_STEP_5.md)**
