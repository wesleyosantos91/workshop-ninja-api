# 📌 Padrão de Organização de Pastas (API)

Este projeto segue um padrão que separa **interface**, **domínio** e **infraestrutura**, com módulos *cross-cutting* (métricas, validação, aspectos) e versionamento da API.  

---

## 📂 Estrutura de Pastas

```
src/main/java/br/org/soujava/bsb/apidozero
├── api/                      # Interface pública (HTTP)
│   ├── exception/            # Tradução de exceções -> respostas HTTP
│   └── v1/
│       ├── controller/       # Endpoints REST
│       ├── request/          # DTOs de entrada
│       └── response/         # DTOs de saída
│
├── core/                     # Cross-cutting e utilitários
│   ├── mapper/               # MapStruct / DTO <-> domínio
│   └── validation/           # Validadores e grupos
│
├── domain/                   # Regras de negócio
│   ├── entity/               # Entidades/JPA
│   ├── exception/            # Exceções de negócio
│   ├── model/                # Objetos de domínio (value objects, aggregates)
│   ├── repository/           # Portas de persistência
│   └── service/              # Casos de uso
│
├── infrastructure/           # Adaptações técnicas (portas externas)
│   └── openapi/              # Config SpringDoc (Swagger)
└── Application.java          # Bootstrap Spring Boot
```

---