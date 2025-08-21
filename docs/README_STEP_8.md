# Passo 8 - Validações (Bean Validation)

## O que vamos fazer
Vamos adicionar validações nos DTOs para garantir que os dados enviados para a API estejam corretos e completos, usando Bean Validation com grupos para diferentes contextos.

## 1) Por que usar Bean Validation?

**Bean Validation** é o padrão Java (JSR 303/380) para validação de dados que oferece:

**Sem validações:**
- API aceita qualquer dado (inclusive vazios ou inválidos)  
- Podem salvar dados incorretos no banco
- Dificulta encontrar problemas
- Código de validação espalhado por toda aplicação

**Com Bean Validation:**
- ✅ **Dados sempre corretos** - Validações automáticas antes de processar
- ✅ **Mensagens padronizadas** - Erros claros sobre o que está incorreto
- ✅ **Anotações declarativas** - Validações próximas aos campos
- ✅ **Reutilização** - Mesmas validações em diferentes contextos
- ✅ **Integração Spring** - Funcionamento automático com controllers
- ✅ **Padrão internacional** - JSR 380 reconhecido mundialmente

## 2) Adicionando a dependência Spring Boot Validation

Primeiro, precisamos adicionar a dependência do Bean Validation no `pom.xml`:

```xml
<!-- Spring Boot Starter Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

**Por que essa dependência?**
- ✅ **Hibernate Validator** - Implementação de referência do Bean Validation
- ✅ **Integração automática** - Funciona automaticamente com Spring Boot
- ✅ **Anotações completas** - Todas as anotações JSR 380 disponíveis
- ✅ **Mensagens internacionalizadas** - Suporte a múltiplos idiomas

## 3) Adicionando validações no NinjaRequest

### 3.1) Criando a classe Groups

Primeiro, vamos criar uma classe para grupos de validação, que nos permite aplicar diferentes validações em diferentes contextos.

Crie `src/main/java/br/org/soujava/bsb/api/core/validation/Groups.java`:

```java
package br.org.soujava.bsb.api.core.validation;

/**
 * Grupos de validação para Bean Validation.
 * Permite aplicar diferentes validações para diferentes contextos.
 */
public class Groups {
    
    /**
     * Grupo usado para validações na criação de recursos.
     */
    public interface Create {}
    
    /**
     * Grupo usado para validações na atualização de recursos.
     */
    public interface Update {}
}
```

**Por que usar grupos de validação?**
- ✅ **Flexibilidade** - Diferentes regras para criação vs atualização
- ✅ **Organização** - Agrupa validações por contexto
- ✅ **Controle** - Escolhe quais validações aplicar em cada endpoint
- ✅ **Cenários reais** - Na criação todos os campos são obrigatórios, na atualização podem ser opcionais

**Exemplo prático:**
```java
// Na CRIAÇÃO: nome deve ser obrigatório
@NotBlank(groups = Groups.Create.class)

// Na ATUALIZAÇÃO: nome pode ser opcional (atualização parcial)
// Sem grupos = não valida na atualização
```

### 3.2) Atualizando o NinjaRequest com validações completas

Agora vamos atualizar o `NinjaRequest` com validações baseadas na estrutura da tabela NINJA:

Edite `src/main/java/br/org/soujava/bsb/api/api/v1/request/NinjaRequest.java`:

```java
package br.org.soujava.bsb.api.api.v1.request;

import br.org.soujava.bsb.api.core.validation.Groups;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record NinjaRequest(
    
    @NotBlank(message = "Nome é obrigatório", groups = Groups.Create.class)
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres", groups = Groups.Create.class)
    String nome,
    
    @NotBlank(message = "Vila é obrigatória", groups = Groups.Create.class)
    @Size(max = 50, message = "Vila deve ter no máximo 50 caracteres", groups = Groups.Create.class)
    String vila,
    
    @Size(max = 50, message = "Clã deve ter no máximo 50 caracteres", groups = Groups.Create.class)
    String cla,
    
    @NotBlank(message = "Rank é obrigatório", groups = Groups.Create.class)
    @Size(max = 20, message = "Rank deve ter no máximo 20 caracteres", groups = Groups.Create.class)
    @Pattern(regexp = "^(Genin|Chunin|Jounin|Kage)$", 
             message = "Rank deve ser: Genin, Chunin, Jounin ou Kage", 
             groups = Groups.Create.class)
    String rank,
    
    @NotBlank(message = "Tipo de chakra é obrigatório", groups = Groups.Create.class)
    @Size(max = 30, message = "Tipo de chakra deve ter no máximo 30 caracteres", groups = Groups.Create.class)
    String chakraTipo,
    
    @Size(max = 50, message = "Especialidade deve ter no máximo 50 caracteres", groups = Groups.Create.class)
    String especialidade,
    
    @Size(max = 50, message = "Kekkei Genkai deve ter no máximo 50 caracteres", groups = Groups.Create.class)
    String kekkeiGenkai,
    
    @Size(max = 20, message = "Status deve ter no máximo 20 caracteres", groups = Groups.Create.class)
    @Pattern(regexp = "^(Ativo|Desaparecido|Renegado)$", 
             message = "Status deve ser: Ativo, Desaparecido ou Renegado", 
             groups = Groups.Create.class)
    String status,
    
    @Min(value = 1, message = "Nível de força deve ser no mínimo 1", groups = Groups.Create.class)
    @Max(value = 100, message = "Nível de força deve ser no máximo 100", groups = Groups.Create.class)
    Integer nivelForca,
    
    @PastOrPresent(message = "Data de registro deve ser hoje ou no passado", groups = Groups.Create.class)
    LocalDate dataRegistro
) {}
```

**Validações aplicadas baseadas na estrutura da tabela:**

| Campo da Tabela NINJA | Validações Bean Validation | Justificativa |
|----------------------|---------------------------|---------------|
| `nome VARCHAR(100) NOT NULL` | `@NotBlank`, `@Size(max=100)` | Campo obrigatório com limite de 100 caracteres |
| `vila VARCHAR(50) NOT NULL` | `@NotBlank`, `@Size(max=50)` | Campo obrigatório com limite de 50 caracteres |
| `cla VARCHAR(50)` | `@Size(max=50)` | Campo opcional, mas limitado a 50 caracteres |
| `rank VARCHAR(20) NOT NULL` | `@NotBlank`, `@Size(max=20)`, `@Pattern` | Obrigatório, limitado e com valores específicos |
| `chakra_tipo VARCHAR(30) NOT NULL` | `@NotBlank`, `@Size(max=30)` | Campo obrigatório com limite de 30 caracteres |
| `especialidade VARCHAR(50)` | `@Size(max=50)` | Campo opcional com limite |
| `kekkei_genkai VARCHAR(50)` | `@Size(max=50)` | Campo opcional com limite |
| `status VARCHAR(20) DEFAULT 'Ativo'` | `@Size(max=20)`, `@Pattern` | Valores específicos permitidos |
| `nivel_forca INT CHECK (BETWEEN 1 AND 100)` | `@Min(1)`, `@Max(100)` | Constraint do banco replicada em Java |
| `data_registro DATE DEFAULT CURRENT_DATE` | `@PastOrPresent` | Data não pode ser no futuro |

## 4) Principais anotações Bean Validation

**Validações de obrigatoriedade:**
- `@NotNull` - Campo não pode ser nulo
- `@NotBlank` - Campo não pode ser nulo, vazio ou só espaços (String)
- `@NotEmpty` - Campo não pode ser nulo ou vazio (Collections, Arrays)

**Validações de tamanho:**
- `@Size(min=, max=)` - Tamanho mínimo e máximo (String, Collections)
- `@Length(min=, max=)` - Tamanho para Strings (Hibernate Validator)

**Validações numéricas:**
- `@Min(value)` - Valor mínimo para números
- `@Max(value)` - Valor máximo para números
- `@Positive` - Deve ser um número positivo (> 0)
- `@PositiveOrZero` - Deve ser positivo ou zero (>= 0)
- `@Negative` - Deve ser um número negativo (< 0)
- `@DecimalMin` / `@DecimalMax` - Para números decimais

**Validações de formato:**
- `@Pattern(regexp)` - Deve seguir uma expressão regular
- `@Email` - Deve ser um email válido
- `@URL` - Deve ser uma URL válida

**Validações de data/tempo:**
- `@Past` - Data deve ser no passado
- `@PastOrPresent` - Data deve ser passado ou presente  
- `@Future` - Data deve ser no futuro
- `@FutureOrPresent` - Data deve ser futuro ou presente

## 5) Ativando validações no Controller

Para que as validações funcionem, precisamos usar `@Validated` com o grupo no controller:

Edite o `NinjaController.java`:

```java
// ...existing code...

// POST /api/v1/ninjas - CRIAR NOVO
@PostMapping
public ResponseEntity<NinjaResponse> create(
        @Validated(Groups.Create.class) @RequestBody NinjaRequest request) {
    NinjaResponse ninja = ninjaService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ninja);
}

// PUT /api/v1/ninjas/1 - ATUALIZAR
@PutMapping("/{id}")
public ResponseEntity<NinjaResponse> update(
        @PathVariable Integer id, 
        @Validated(Groups.Update.class) @RequestBody NinjaRequest request) {
    NinjaResponse ninja = ninjaService.update(id, request);
    return ResponseEntity.ok(ninja);
}

// ...existing code...
```

**Diferença entre @Valid e @Validated:**
- `@Valid` - Valida usando o grupo padrão (todas as validações sem grupos)
- `@Validated(Groups.Create.class)` - Valida apenas as validações do grupo Create
- `@Validated({Groups.Create.class, Groups.Update.class})` - Valida múltiplos grupos

## 6) Testando as validações

Compile o projeto para incluir a nova dependência:
```bash
./mvnw compile
```

Execute a aplicação e teste diferentes cenários:

**Ninja sem nome (deve falhar):**
```bash
curl -X POST http://localhost:8080/api/v1/ninjas \
  -H "Content-Type: application/json" \
  -d '{
    "vila": "Konoha",
    "rank": "Genin",
    "chakra_tipo": "Fogo"
  }'
```

**Resposta esperada (RFC 9457 Problem Details):**
```json
{
  "type": "about:blank",
  "title": "Validation failed",
  "status": 400,
  "detail": "The following errors occurred:",
  "timestamp": "2024-01-20T10:15:30.123456Z",
  "errors": [
    {
      "field": "nome",
      "message_error": "Nome é obrigatório"
    }
  ]
}
```

**Rank inválido (deve falhar):**
```bash
curl -X POST http://localhost:8080/api/v1/ninjas \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Teste Ninja",
    "vila": "Konoha", 
    "rank": "SuperNinja",
    "chakra_tipo": "Fogo"
  }'
```

**Resposta esperada:**
```json
{
  "type": "about:blank",
  "title": "Validation failed", 
  "status": 400,
  "detail": "The following errors occurred:",
  "timestamp": "2024-01-20T10:15:30.123456Z",
  "errors": [
    {
      "field": "rank",
      "message_error": "Rank deve ser: Genin, Chunin, Jounin ou Kage"
    }
  ]
}
```

**Dados válidos (deve funcionar):**
```bash
curl -X POST http://localhost:8080/api/v1/ninjas \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Rock Lee",
    "vila": "Konoha",
    "cla": null,
    "rank": "Chunin", 
    "chakra_tipo": "Terra",
    "especialidade": "Taijutsu",
    "kekkei_genkai": null,
    "status": "Ativo",
    "nivel_forca": 75
  }'
```

## 7) Vantagens da implementação com grupos

✅ **Flexibilidade por contexto** - Criação vs Atualização têm regras diferentes
✅ **Reutilização** - Mesmo DTO para diferentes endpoints  
✅ **Manutenibilidade** - Validações centralizadas no DTO
✅ **Alinhamento com banco** - Validações espelham constraints da tabela
✅ **Mensagens claras** - Usuário sabe exatamente o que corrigir
✅ **Performance** - Validações acontecem antes do processamento
✅ **Padrão industrial** - Bean Validation é amplamente adotado

## 8) Validações personalizadas (avançado)

Se precisar de validações mais específicas, pode criar suas próprias:

```java
// Exemplo de validação customizada
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)  
@Constraint(validatedBy = NinjaNameValidator.class)
public @interface ValidNinjaName {
    String message() default "Nome de ninja inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// Implementação da validação
public class NinjaNameValidator implements ConstraintValidator<ValidNinjaName, String> {
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true; // @NotBlank já valida isso
        
        // Regra: nome não pode ter números
        return !value.matches(".*\\d.*");
    }
}
```

## Próximo passo
Agora vamos adicionar validações nos DTOs para garantir que os dados enviados estejam corretos. **[STEP 9 — OpenAPI / Swagger](README_STEP_9.md)**