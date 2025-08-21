<div align="center">
  <img src="docs/asserts/logo-soujava.png" alt="SouJava Brasília" width="200"/>

# 🥷 API do Zero - Workshop Ninja API

**Workshop "Do Zero à API" - SouJava Brasília**

Uma API REST completa sobre ninjas do universo Naruto, construída passo a passo com Spring Boot

[![](https://img.shields.io/badge/Autor-Wesley%20Oliveira%20Santos-brightgreen)](https://www.linkedin.com/in/wesleyosantos91/)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![H2](https://img.shields.io/badge/Database-H2-lightblue.svg)](https://www.h2database.com/)

</div>

## 📋 Sobre o Projeto

Este projeto é um **workshop prático** que ensina como criar uma API REST completa do zero usando Spring Boot. Você vai
aprender construindo uma API sobre ninjas do universo Naruto, com todas as operações CRUD e melhores práticas de
desenvolvimento.

### 🎯 O que você vai aprender:

- ✅ Configuração de projeto Spring Boot
- ✅ Banco de dados H2 (em memória)
- ✅ Entidades JPA e Repositories
- ✅ DTOs e Mappers (MapStruct)
- ✅ Services com lógica de negócio
- ✅ Controllers REST
- ✅ Tratamento de erros
- ✅ Validações de entrada
- ✅ Documentação com Swagger

### 🛠️ Tecnologias Utilizadas:

- **Java 21** - Linguagem de programação
- **Spring Boot 3.5.4** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **H2 Database** - Banco de dados em memória
- **MapStruct** - Mapeamento automático de objetos
- **Bean Validation** - Validação de dados
- **SpringDoc OpenAPI** - Documentação automática (Swagger)
- **Maven** - Gerenciador de dependências

## 🚀 Como Executar

### Pré-requisitos:

- Java 21 ou superior
- Maven 3.6 ou superior (opcional, o projeto inclui Maven Wrapper)
- Uma IDE (IntelliJ IDEA, Eclipse, VS Code)

### Executando a aplicação:

1. **Clone o repositório:**

```bash
git clone https://github.com/wesleyosantos91/workshop-ninja-api.git
cd api-do-zero
```

2. **Execute a aplicação:**

```bash
# No Windows:
mvnw.cmd spring-boot:run

# No Linux/Mac:
./mvnw spring-boot:run
```

3. **Acesse as URLs:**

- **API:** http://localhost:8080/api/v1/ninjas
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **H2 Console:** http://localhost:8080/h2
    - URL JDBC: `jdbc:h2:mem:naruto`
    - Username: `sa`
    - Password: (deixe vazio)

## 📚 Workshop - Guia Passo a Passo

O workshop está dividido em 9 passos progressivos. Cada passo tem seu próprio README detalhado:

| Passo | Descrição                                | README                                    |
|-------|------------------------------------------|-------------------------------------------|
| **1** | Configuração Inicial                     | [README_STEP_1.md](docs/README_STEP_1.md) |
| **2** | Banco de Dados (H2 + SQL)                | [README_STEP_2.md](docs/README_STEP_2.md) |
| **3** | Entity e Repository                      | [README_STEP_3.md](docs/README_STEP_3.md) |
| **4** | DTOs e Mapper                            | [README_STEP_4.md](docs/README_STEP_4.md) |
| **5** | Service (Lógica de Negócio)              | [README_STEP_5.md](docs/README_STEP_5.md) |
| **6** | Controller E validaçoes (Endpoints REST) | [README_STEP_6.md](docs/README_STEP_6.md) |
| **7** | Tratamento de Erros                      | [README_STEP_7.md](docs/README_STEP_7.md) |
| **8** | Documentação (Swagger)                   | [README_STEP_8.md](docs/README_STEP_8.md) |
| **9** | Revisão Final                            | [README_STEP_9.md](docs/README_STEP_9.md) |

### 🎓 Como seguir o workshop:

1. Leia cada README na ordem
2. Implemente o código de cada passo
3. Teste as funcionalidades
4. Passe para o próximo passo

## 🥷 API Endpoints

### Ninjas CRUD:

- **GET** `/api/v1/ninjas` - Lista todos os ninjas
- **GET** `/api/v1/ninjas/{id}` - Busca ninja por ID
- **GET** `/api/v1/ninjas/search` - Busca com filtros
- **POST** `/api/v1/ninjas` - Cria novo ninja
- **PUT** `/api/v1/ninjas/{id}` - Atualiza ninja
- **DELETE** `/api/v1/ninjas/{id}` - Deleta ninja

### Exemplo de JSON (Ninja):

```json
{
  "id": 1,
  "nome": "Naruto Uzumaki",
  "vila": "Konoha",
  "cla": "Uzumaki",
  "rank": "Genin",
  "chakra_tipo": "Vento",
  "especialidade": "Ninjutsu",
  "kekkei_genkai": "Rasengan",
  "status": "Ativo",
  "nivel_forca": 85,
  "data_registro": "2023-01-01"
}
```

## 🧪 Testando a API

### Usando curl:

```bash
# Listar todos os ninjas
curl http://localhost:8080/api/v1/ninjas

# Buscar ninja específico
curl http://localhost:8080/api/v1/ninjas/1

# Criar novo ninja
curl -X POST http://localhost:8080/api/v1/ninjas \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Meu Ninja",
    "vila": "Konoha",
    "rank": "Genin",
    "chakra_tipo": "Fogo"
  }'

# Buscar com filtros
curl "http://localhost:8080/api/v1/ninjas/search?vila=Konoha&rank=Genin"
```

### Usando Swagger UI:

1. Acesse: http://localhost:8080/swagger-ui.html
2. Explore e teste todos os endpoints diretamente no navegador

## 🏗️ Arquitetura do Projeto

```
src/main/java/br/org/soujava/bsb/api/
├── api/                    # Camada de apresentação (Controllers, DTOs)
│   ├── exception/          # Tratamento global de erros
│   └── v1/                 # Versão 1 da API
│       ├── controller/     # Controllers REST
│       ├── request/        # DTOs de entrada
│       └── response/       # DTOs de saída
├── core/                   # Utilitários e configurações
│   └── mapper/             # Mappers (MapStruct)
├── domain/                 # Camada de domínio (lógica de negócio)
│   ├── entity/             # Entidades JPA
│   ├── repository/         # Repositories
│   ├── service/            # Services
│   └── exception/          # Exceções de negócio
└── infrastructure/         # Configurações de infraestrutura
    └── openapi/            # Configuração do Swagger
```

## 💡 Conceitos Aprendidos

### Padrões de Arquitetura:

- **MVC** (Model-View-Controller)
- **Repository Pattern**
- **DTO Pattern**
- **Service Layer Pattern**

### Boas Práticas:

- Separação de responsabilidades
- Tratamento centralizado de erros
- Validação de dados de entrada
- Documentação automática
- Mapeamento automático de objetos

## 🎯 Próximos Passos (após completar o workshop)

- 🔒 **Segurança:** Spring Security + JWT
- 🗄️ **Banco Real:** PostgreSQL com Docker
- ✅ **Testes:** JUnit + Mockito + Testcontainers
- 📊 **Monitoramento:** Spring Actuator
- 🚀 **Deploy:** Docker + Cloud

## 👥 Comunidade

- **SouJava Brasília:** [Site oficial](https://soujava.org.br/)
- **WhatsAPP:** [Organização SouJava BSB](https://chat.whatsapp.com/J7OkMG4s9V8Gc8YQgnNgnz)
- **Meetups:** Participe dos nossos encontros presenciais e online

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

<div align="center">
  <p>Feito com ❤️ para a comunidade Java por <strong>SouJava Brasília</strong></p>
  <p>⭐ Se este projeto te ajudou, considere dar uma estrela!</p>
</br>
<a href="https://www.linkedin.com/in/wesleyosantos91/" target="_blank">
  <img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white" target="_blank" />
</a>

</br>
<b>Developed by Wesley Oliveira Santos</b>
</div>
