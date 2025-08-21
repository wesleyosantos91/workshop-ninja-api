

<div align="center" width="100%">
    <img src="docs/asserts/logo-soujava.png" alt="logo" width="200" height="auto" />
</div>

<div align="center">

![](https://img.shields.io/badge/Autor-Wesley%20Oliveira%20Santos-brightgreen)
![](https://img.shields.io/badge/Language-Java%2021-brightgreen)
![](https://img.shields.io/badge/Framework-Spring%20Boot%203.5.4-brightgreen)
![](https://img.shields.io/badge/Build-Maven-blue)
![](https://img.shields.io/badge/Database-H2-lightgrey)
![](https://img.shields.io/badge/Mapper-MapStruct-orange)
![](https://img.shields.io/badge/Docs-Springdoc%20OpenAPI-yellow)
![](https://img.shields.io/badge/Testing-JUnit%205-red)
![](https://img.shields.io/badge/Mock-MockMvc%20%26%20Mockito-critical)
![](https://img.shields.io/badge/License-MIT-green)

<p>
  <a href="https://github.com/wesleyosantos91/workshop-ninja-api/graphs/contributors">
    <img src="https://img.shields.io/github/contributors/wesleyosantos91/workshop-ninja-api" alt="contributors" />
  </a>
  <a href="">
    <img src="https://img.shields.io/github/last-commit/wesleyosantos91/workshop-ninja-api" alt="last update" />
  </a>
  <a href="https://github.com/wesleyosantos91/workshop-ninja-api/network/members">
    <img src="https://img.shields.io/github/forks/wesleyosantos91/workshop-ninja-api" alt="forks" />
  </a>
  <a href="https://github.com/wesleyosantos91/workshop-ninja-api/stargazers">
    <img src="https://img.shields.io/github/stars/wesleyosantos91/workshop-ninja-api" alt="stars" />
  </a>
  <a href="https://github.com/wesleyosantos91/workshop-ninja-api/issues/">
    <img src="https://img.shields.io/github/issues/wesleyosantos91/workshop-ninja-api" alt="open issues" />
  </a>
  <a href="https://github.com/wesleyosantos91/workshop-ninja-api/pulls/">
    <img src="https://img.shields.io/github/issues-pr/wesleyosantos91/workshop-ninja-api" alt="pull requests" />
  </a>
  <a href="https://github.com/wesleyosantos91/workshop-ninja-api/blob/main/LICENSE">
    <img src="https://img.shields.io/github/license/wesleyosantos91/workshop-ninja-api" alt="license" />
  </a>
</p>

</div>

##  Pré - requisitos

- [ `Java 21+` ](https://www.oracle.com/java/technologies/downloads/#java21)
- [ `Apache Maven`](https://maven.apache.org/download.cgi)

# Do Zero à API — Plano Didático (Principal)

Este documento **principal** organiza o projeto em **9 etapas** para ensino passo a passo.  
Cada etapa terá seu próprio arquivo com instruções detalhadas dentro de `docs/`.

> Projeto base
>
> - **GroupId**: `br.org.soujava.bsb`
> - **ArtifactId**: `workshop-ninja-api`
> - **Nome**: `apidozero`
> - **Java**: 21
> - **Descrição**: API RESTful de domínio ninja desenvolvida com Spring Boot — Workshop “Do Zero à API” SouJava Brasília
>
> Dependências principais já no projeto:
> - Spring Web, Spring Data JPA, H2, MapStruct, springdoc-openapi, Spring Boot Test

---

## Como usar em aula

1. **Apresente o Initializr**: mostre https://start.spring.io/ e demonstre como configurar um projeto idêntico ao do repositório (sem baixar nada, apenas para contextualizar).
2. **Siga os steps**: peça para os alunos abrirem cada `docs/README_STEP_X.md` na ordem e executarem o que está descrito.
3. **Código real**: os READMEs devem sempre usar **arquivos e nomes reais** do repo (nada inventado).
4. **Validação constante**: ao final de cada etapa, há um **Checklist** com o que deve estar funcionando.

---

## Índice das Etapas

1. **[STEP 1 — Configuração Inicial](docs/README_STEP_1.md)**  
   Gerar/explicar projeto via Initializr, `application.yml` fiel ao repo, subir com `./mvnw spring-boot:run`, validar `/h2` e Swagger UI.

2. **[STEP 2 — Persistência (H2 + schema.sql + data.sql)](docs/README_STEP_2.md)**  
   Configurar e explicar H2, DDL em `schema.sql` e carga inicial com `data.sql`. Validar via console H2.

3. **[STEP 3 — Entidade e Repository](docs/README_STEP_3.md)**  
   Documentar `NinjaEntity` e `NinjaRepository`, constraints e mapeamentos. Testes de repositório com H2 (`@DataJpaTest`).

4. **[STEP 4 — DTOs](docs/README_STEP_4.md)**  
   `NinjaRequest`, `NinjaQueryRequest`, `NinjaResponse` (payloads em **snake_case**). Trafego no Controller e mapeamento com MapStruct.

5. **[STEP 5 — Service](docs/README_STEP_5.md)**  
   `NinjaService` com operações `create`, `find`, `update`, `delete`, `search`. Testes unitários com Mockito.

6. **[STEP 6 — Controller](docs/README_STEP_6.md)**  
   Endpoints reais em `/v1/ninjas` (POST/GET/PUT/DELETE/GET paginado), exemplos `curl/httpie`, testes com `MockMvc`.

7. **[STEP 7 — Tratamento de Erros](docs/README_STEP_7.md)**  
   `ApiExceptionHandler`, payload de erro padronizado, testes de casos 400/404.

8. **[STEP 8 — OpenAPI / Swagger](docs/README_STEP_8.md)**  
   `OpenAPIConfig`, validação do Swagger UI (`/swagger-ui/index.html#/`), exemplos de schemas e responses.

9. **[STEP 9 — Revisão Final & Próximos Passos](docs/README_STEP_9.md)**  
   Checklist geral (app, Swagger, H2, testes verdes), troubleshooting e sugestões (Testcontainers, profiles, observabilidade).

---

## Quick Start (do projeto já pronto)

```bash
./mvnw spring-boot:run
# ou
./mvnw clean package
java -jar target/*.jar
```

- H2 Console: `http://localhost:8080/h2`
    - JDBC URL: `jdbc:h2:mem:naruto;MODE=MYSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
    - user: `sa`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html#/`

---

## Estrutura esperada dos READMEs (por etapa)

Cada `docs/README_STEP_X.md` deve conter:
1. **Objetivo**
2. **Arquivos criados/editados (caminhos reais)**
3. **Explicação conceitual** (tom acadêmico)
4. **Trechos de código reais** (copiados do repo)
5. **Como rodar e validar** (comandos + URLs)
6. **Testes** (o que cobrem e como rodar: `./mvnw test`)
7. **Erros comuns & soluções**
8. **Resultados esperados / evidências**

---


<a href="https://www.linkedin.com/in/wesleyosantos91/" target="_blank">
  <img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white" target="_blank" />
</a>


<b>Developed by Wesley Oliveira Santos</b>
---
