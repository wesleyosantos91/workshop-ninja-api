# Passo 7 - Tratamento de Erros (RFC 9457 - Problem Details)

## O que vamos fazer
Vamos criar um tratamento global de erros seguindo o padrão RFC 9457 (Problem Details for HTTP APIs) para que nossa API retorne respostas de erro padronizadas e profissionais.

## 1) Por que tratar erros com RFC 9457?

**RFC 9457 - Problem Details for HTTP APIs** é um padrão que define como estruturar respostas de erro em APIs REST de forma consistente e útil.

**Sem tratamento padronizado:**
- Mensagens confusas e técnicas
- Formatos diferentes para cada tipo de erro
- Informações técnicas desnecessárias expostas

**Com RFC 9457:**
- ✅ **Formato padronizado** - Estrutura consistente para todos os erros
- ✅ **Informações úteis** - Detalhes claros sobre o problema
- ✅ **Compatibilidade** - Padrão reconhecido internacionalmente
- ✅ **Extensibilidade** - Permite adicionar campos customizados

## 2) Estrutura da RFC 9457

A RFC 9457 define campos padronizados para respostas de erro:

```json
{
  "type": "https://example.com/probs/out-of-credit",
  "title": "You do not have enough credit.",
  "status": 403,
  "detail": "Your current balance is 30, but that costs 50.",
  "instance": "/account/12345/msgs/abc"
}
```

**Campos principais:**
- **type** - URI que identifica o tipo do problema
- **title** - Resumo curto do problema
- **status** - Código de status HTTP
- **detail** - Explicação detalhada do problema
- **instance** - URI que identifica onde o problema ocorreu

## 3) Implementação no Spring Boot

O Spring Boot 6+ tem suporte nativo à RFC 9457 através da classe `ProblemDetail`. Vamos implementar dois tipos de tratamento:

### 3.1) Criando a classe ErrorResponse

Primeiro, crie `src/main/java/br/org/soujava/bsb/api/api/v1/response/ErrorResponse.java`:

```java
package br.org.soujava.bsb.api.api.v1.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ErrorResponse(
        String field,
        String messageError) {
}
```

### 3.2) Criando CustomProblemDetail para validações

Para erros de validação com múltiplos campos, criamos uma versão customizada:

Crie `src/main/java/br/org/soujava/bsb/api/api/v1/response/CustomProblemDetail.java`:

```java
package br.org.soujava.bsb.api.api.v1.response;

import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class CustomProblemDetail extends ProblemDetail {

    private static final String TIMESTAMP = "timestamp";

    public CustomProblemDetail(HttpStatus status, String title, String detail, List<ErrorResponse> errors) {
        this.setStatus(status.value());
        this.setTitle(title);
        this.setDetail(detail);
        this.setProperty(TIMESTAMP, Instant.now());
        this.setProperty("errors", errors);
    }
}
```

**Por que CustomProblemDetail?**
- ✅ **Estende ProblemDetail** - Mantém compatibilidade com RFC 9457
- ✅ **Timestamp automático** - Adiciona quando o erro ocorreu
- ✅ **Lista de erros** - Para validações com múltiplos campos
- ✅ **Flexível** - Pode adicionar outros campos personalizados

### 3.3) Criando o ApiExceptionHandler

Agora vamos criar o tratador global de exceções:

Crie `src/main/java/br/org/soujava/bsb/api/api/exception/ApiExceptionHandler.java`:

```java
package br.org.soujava.bsb.api.api.exception;

import br.org.soujava.bsb.api.api.v1.response.CustomProblemDetail;
import br.org.soujava.bsb.api.api.v1.response.ErrorResponse;
import br.org.soujava.bsb.api.domain.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.filter.ServerHttpObservationFilter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);
    private final MessageSource messageSource;

    public ApiExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    // Trata erros de validação (400 - Bad Request)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        // Converte erros de validação para nossa estrutura
        final List<ErrorResponse> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new ErrorResponse(
                    fieldError.getField(), 
                    messageSource.getMessage(fieldError, LocaleContextHolder.getLocale())
                ))
                .toList();

        // Cria CustomProblemDetail com lista de erros
        final CustomProblemDetail problemDetail = new CustomProblemDetail(
            HttpStatus.BAD_REQUEST,
            "Validation failed", 
            "The following errors occurred:", 
            errors
        );

        // Adiciona observabilidade (logs, métricas)
        final HttpServletRequest httpServletRequest = ((ServletWebRequest) request).getRequest();
        ServerHttpObservationFilter.findObservationContext(httpServletRequest)
                .ifPresent(context -> context.setError(ex));

        LOGGER.error("Validation failed: {}", errors);

        return super.handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    // Trata recurso não encontrado (404 - Not Found)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    private ResponseEntity<ProblemDetail> handleResourceNotFoundException(
            HttpServletRequest request, 
            ResourceNotFoundException ex) {

        // Cria ProblemDetail padrão da RFC 9457
        final ProblemDetail problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle(HttpStatus.NOT_FOUND.getReasonPhrase());

        // Adiciona observabilidade
        ServerHttpObservationFilter.findObservationContext(request)
                .ifPresent(context -> context.setError(ex));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }
}
```

## 4) Como funciona a implementação

**@RestControllerAdvice**
- Aplica o tratamento a todos os controllers da aplicação
- Herda de `ResponseEntityExceptionHandler` para usar as funcionalidades do Spring

**@ExceptionHandler** vs **@Override**
- `@ExceptionHandler` - Para exceções customizadas (ResourceNotFoundException)
- `@Override` - Para exceções padrão do Spring (MethodArgumentNotValidException)

**MessageSource**
- Permite internacionalização das mensagens de erro
- Converte códigos de erro em mensagens amigáveis

**ServerHttpObservationFilter**
- Adiciona observabilidade (logs, métricas, tracing)
- Fundamental para monitoramento em produção

## 5) Exemplos de resposta seguindo RFC 9457

### 5.1) Erro de recurso não encontrado (404):
```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Ninja não encontrado com ID: 999",
  "instance": "/api/v1/ninjas/999"
}
```

### 5.2) Erro de validação (400):
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
    },
    {
      "field": "nivel_forca",
      "message_error": "Nível de força deve estar entre 1 e 100"
    }
  ]
}
```

## 6) Vantagens da implementação com RFC 9457

✅ **Padrão internacional** - Reconhecido e usado mundialmente
✅ **Consistência** - Todos os erros seguem a mesma estrutura
✅ **Extensível** - Pode adicionar campos customizados (timestamp, errors)
✅ **Observabilidade** - Integração com logs e métricas
✅ **Internacionalização** - Mensagens em diferentes idiomas
✅ **Compatibilidade** - Clientes sabem como interpretar os erros

## 7) Testando o tratamento de erro

1. Execute a aplicação
2. Teste diferentes cenários:

**Ninja não encontrado:**
```bash
curl http://localhost:8080/api/v1/ninjas/999
```

**Dados inválidos:**
```bash
curl -X POST http://localhost:8080/api/v1/ninjas \
  -H "Content-Type: application/json" \
  -d '{"nome": "", "nivel_forca": 150}'
```

## 8) Outros tipos de erro que você pode adicionar

```java
// Para problemas de acesso ao banco
@ExceptionHandler(DataAccessException.class)
public ResponseEntity<ProblemDetail> handleDataAccess(
        HttpServletRequest request, DataAccessException ex) {
    
    ProblemDetail problemDetail = ProblemDetail
            .forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, 
                              "Erro interno do servidor");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                         .body(problemDetail);
}

// Para problemas de conversão de JSON
@ExceptionHandler(HttpMessageNotReadableException.class)
public ResponseEntity<ProblemDetail> handleInvalidJson(
        HttpServletRequest request, HttpMessageNotReadableException ex) {
    
    ProblemDetail problemDetail = ProblemDetail
            .forStatusAndDetail(HttpStatus.BAD_REQUEST, 
                              "JSON inválido ou malformado");
    return ResponseEntity.badRequest().body(problemDetail);
}
```

## Próximo passo
Agora vamos adicionar validações nos DTOs para garantir que os dados enviados estejam corretos. **[STEP 8 — Validações (Bean Validation)](README_STEP_8.md)**
