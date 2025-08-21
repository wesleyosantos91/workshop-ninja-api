# Passo 1 - Configuração Inicial

## O que vamos fazer
Vamos configurar o projeto Spring Boot básico com as dependências essenciais e testar se está funcionando.

## 1) Conhecendo o Spring Initializr

O Spring Initializr (https://start.spring.io/) é uma ferramenta que cria projetos Spring Boot automaticamente. 

Para este workshop, você configuraria assim:

- **Projeto:** Maven (gerenciador de dependências)
- **Linguagem:** Java
- **Spring Boot:** 3.5.4
- **Java:** 21
- **Dependências iniciais:**
  - Spring Web (para criar APIs REST)
  - Spring Data JPA (para trabalhar com banco de dados)
  - H2 Database (banco de dados em memória)

## 2) Dependências do Step 1

Neste primeiro passo, vamos usar apenas as dependências essenciais:

- **Spring Web** - para criar endpoints REST
- **Spring Data JPA** - para acessar o banco de dados
- **H2 Database** - banco de dados temporário (só existe na memória)

**Outras dependências serão adicionadas nos próximos passos:**
- MapStruct (Step 4 - para conversão automática entre objetos)
- SpringDoc OpenAPI (Step 8 - para documentação Swagger)
- Bean Validation (Step 8 - para validações)

## 3) Configuração do banco H2

O arquivo `application.yml` já está configurado com:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:naruto
    driverClassName: org.h2.Driver
    username: sa
    password: 
  h2:
    console:
      enabled: true
      path: /h2
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: none
  sql:
    init:
      mode: always
```

**O que isso significa:**
- Banco H2 em memória chamado "naruto"
- Console H2 habilitado em `/h2`
- Não gera tabelas automaticamente (`ddl-auto: none`)
- Executa sempre os arquivos SQL de inicialização

## 4) Executando o projeto

1. Abra o terminal na pasta do projeto
2. Execute: `./mvnw spring-boot:run` (Linux/Mac) ou `mvnw.cmd spring-boot:run` (Windows)
3. Aguarde até ver "Started Application"

## 5) Testando se funcionou

Acesse no navegador:
- **Aplicação:** http://localhost:8080
- **H2 Console:** http://localhost:8080/h2

### Para acessar o H2 Console:
- URL JDBC: `jdbc:h2:mem:naruto`
- Username: `sa`
- Password: (deixe vazio)

**Neste momento você verá:**
- Aplicação rodando (mas ainda sem endpoints)
- Console H2 funcionando (mas sem tabelas ainda)

## Próximo passo
No próximo passo vamos criar as tabelas do banco de dados e inserir alguns dados de exemplo usando `schema.sql` e `data.sql`. **[STEP 2 — Persistência (H2 + schema.sql + data.sql)](README_STEP_2.md)**
