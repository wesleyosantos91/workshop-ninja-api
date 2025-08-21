# README_STEP_1 — Configuração Inicial (alinhado ao projeto)

## Objetivo
Subir a aplicação com a configuração **exata** do repositório, apresentar o **Spring Initializr** aos alunos, e validar rapidamente **H2 Console** e **Swagger UI**.

---

## 1) Mostrar o Spring Initializr em aula
Abra **https://start.spring.io/** e configure (apenas demonstração, sem baixar nada agora):

- **Project:** Maven
- **Language:** Java
- **Spring Boot:** versão estável compatível com Java 21
- **Project Metadata**
  - **Group:** `br.org.soujava.bsb`
  - **Artifact:** `workshop-ninja-api`
  - **Name:** `apidozero`
  - **Description:** `API RESTful de domínio ninja desenvolvida com Spring Boot - Workshop "Do Zero à API" SouJava Brasília`
  - **Package name:** (sua preferência, ex.: `br.org.soujava.bsb.workshopninjaapi`)
  - **Packaging:** Jar
  - **Java:** **21**
- **Dependencies:**
  - Spring Web
  - Spring Data JPA
  - H2 Database

> **Observações didáticas**
> - **MapStruct** e **springdoc-openapi** não estão na lista do Initializr — ensine a turma a adicionar no `pom.xml` após a geração.
> - Nosso repo **já tem** essas dependências, então aqui é só para contextualizar o processo e reforçar os metadados de projeto.

---

## 2) Dependências existentes no projeto
O `pom.xml` do repositório já inclui:
- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `com.h2database:h2` (runtime)
- `org.mapstruct:mapstruct` e `org.mapstruct:mapstruct-processor`
- `org.springdoc:springdoc-openapi-starter-webmvc-ui`
- `spring-boot-starter-test` (test)

> Dica: em projetos novos, configure o `mapstruct-processor` como `annotationProcessor` no `maven-compiler-plugin`. Aqui manteremos **fiel ao repo**.

---

## 3) `application.yml` (usar exatamente este do projeto)

Coloque este arquivo em `src/main/resources/application.yml`:

```yaml
server:
  address: localhost
  port: 8080
  ssl:
    enabled: false
spring:
  application:
    name: api-do-zero
  datasource:
    url: jdbc:h2:mem:naruto;MODE=MYSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    username: sa
    password:
    driver-class-name: org.h2.Driver
  h2:
    console:
      enabled: true
      path: /h2
  sql:
    init:
      mode: always       # executa schema.sql e data.sql no startup
      platform: h2       # habilita sufixos -h2 se quiser
  jpa:
    hibernate:
      ddl-auto: none     # não deixe o Hibernate criar/alterar tabelas
    database-platform: org.hibernate.dialect.H2Dialect


```

**Por que assim?**
- `ddl-auto: none` → deixa o **`schema.sql`** (DDL) mandar no banco.
- `sql.init.mode: always` → executa `schema.sql` e `data.sql` no startup.
- H2 Console habilitado em `/h2` para inspeção rápida.

---

## 4) Rodando o projeto

Na raiz do projeto:

```bash
./mvnw spring-boot:run
```

Opcionalmente, empacote e rode o JAR:

```bash
./mvnw clean package
java -jar target/*.jar
```

---

## 5) Validações rápidas

- **H2 Console:** `http://localhost:8080/h2`
  - JDBC URL: `jdbc:h2:mem:naruto;MODE=MYSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
  - user: `sa` (sem senha)
- **Swagger UI:** `http://localhost:8080/swagger-ui/index.html#/`

> Se a UI não abrir agora, validaremos no **STEP 8** a configuração do `springdoc` e do `OpenAPIConfig` (se houver).

---

## 6) Checklist do STEP 1
- [ ] Projeto compila e sobe com `./mvnw spring-boot:run`
- [ ] `application.yml` igual ao do repositório (H2, JPA, SQL init)
- [ ] H2 Console acessível em `/h2`
- [ ] Swagger UI acessível (ou confirmado para ver no STEP 8)

---

## 7) Erros comuns & soluções
- **Porta 8080 ocupada:** altere `server.port` no `application.yml` (ex.: `8081`).
- **H2 Console 404:** confirme `spring.h2.console.enabled: true` e `path: /h2`.
- **`schema.sql`/`data.sql` não executam:** verifique `spring.sql.init.mode: always` e os arquivos em `src/main/resources`.
- **Driver H2 ausente:** confira a dependência `com.h2database:h2` no `pom.xml`.

---

## 8) Próximo passo
Ir para o **[STEP 2 — Persistência (H2 + schema.sql + data.sql)](README_STEP_2.md)** para criar/validar as tabelas e seeds.
