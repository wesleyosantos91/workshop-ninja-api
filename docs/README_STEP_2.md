# README_STEP_2 — Persistência (H2 + schema.sql + data.sql)

## Objetivo
Configurar e **validar a persistência** usando o banco **H2 em memória**, com DDL no `schema.sql` e carga inicial no `data.sql`, **exatamente como no repositório**.

---

## 1) Contexto rápido (H2 + Spring Boot)
- O arquivo `application.yml` (STEP 1) já está configurado para:
  - Usar H2 em memória: `jdbc:h2:mem:naruto;MODE=MYSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
  - Rodar **sempre** `schema.sql` e `data.sql` no startup: `spring.sql.init.mode=always`
  - `ddl-auto: none` → quem manda é o **`schema.sql`**
  - Console do H2 em `/h2`

---

## 2) DDL real (schema.sql)
Conteúdo do arquivo **real** do projeto (`src/main/resources/schema.sql`):

```sql
CREATE TABLE IF NOT EXISTS NINJA (
    id_ninja       INT AUTO_INCREMENT PRIMARY KEY,  -- Identificador único
    nome           VARCHAR(100) NOT NULL,           -- Nome do ninja
    vila           VARCHAR(50) NOT NULL,            -- Vila de origem
    cla            VARCHAR(50),                     -- Clã
    rank           VARCHAR(20) NOT NULL,            -- Genin, Chunin, Jounin, Kage
    chakra_tipo    VARCHAR(30) NOT NULL,            -- Afinidade elemental
    especialidade  VARCHAR(50),                     -- Ninjutsu, Genjutsu, Taijutsu, Senjutsu
    kekkei_genkai  VARCHAR(50),                     -- Poder de linhagem
    status         VARCHAR(20) DEFAULT 'Ativo',     -- Ativo, Desaparecido, Renegado
    nivel_forca    INT,                             -- 1–100
    data_registro  DATE DEFAULT CURRENT_DATE,       -- Registro
    CONSTRAINT chk_nivel_forca CHECK (nivel_forca BETWEEN 1 AND 100)
);
```

> **Dicas didáticas**
> - Confirme se nomes de tabela/colunas batem com a **NinjaEntity** (STEP 3).
> - Defina PRIMARY KEY, tipos adequados (ex.: `UUID`, `VARCHAR`, `TIMESTAMP`), constraints (NOT NULL, UNIQUE) e índices quando necessário.

---

## 3) Seed real (data.sql)
Conteúdo do arquivo **real** do projeto (`src/main/resources/data.sql`):

```sql
INSERT INTO NINJA (nome, vila, cla, rank, chakra_tipo, especialidade, kekkei_genkai, status, nivel_forca)
VALUES
    ('Naruto Uzumaki', 'Konoha', 'Uzumaki', 'Hokage', 'Vento', 'Ninjutsu', 'Kurama (Bijuu)', 'Ativo', 98),
    ('Sasuke Uchiha', 'Konoha', 'Uchiha', 'Jounin', 'Fogo', 'Ninjutsu/Genjutsu', 'Sharingan/Rinnegan', 'Renegado', 97),
    ('Sakura Haruno', 'Konoha', NULL, 'Jounin', 'Terra', 'Taijutsu/Medicina', NULL, 'Ativo', 85),
    ('Gaara', 'Sunagakure', NULL, 'Kazekage', 'Vento', 'Ninjutsu (Areia)', 'Shukaku (Bijuu)', 'Ativo', 95);

```

> **Boa prática**: mantenha o `data.sql` **mínimo** para testes rápidos (ex.: meia dúzia de registros). Para dados volumosos, use `import.sql` separado ou ferramentas de migração (Flyway/Liquibase).

---

## 4) Como validar no H2 Console

1. Suba a aplicação:
   ```bash
   ./mvnw spring-boot:run
   ```

2. Abra `http://localhost:8080/h2` e preencha:
   - **JDBC URL:** `jdbc:h2:mem:naruto;MODE=MYSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
   - **User:** `sa` (sem senha)

3. Execute consultas simples para validar DDL + seed, por exemplo:
   ```sql
   -- listar tabelas criadas
   SHOW TABLES;

   -- inspecionar a estrutura da(s) tabela(s) principal(is)
   -- ex.: se a tabela for NINJA(S)
   -- DESCRIBE NINJAS;   -- (no H2 use: SHOW COLUMNS FROM NINJAS;)

   -- validar os registros de seed
   SELECT * FROM NINJAS;
   ```

> Ajuste os nomes das consultas conforme os **nomes reais** do seu `schema.sql` (ex.: `NINJAS`, `NINJA`, etc.).

---

## 5) Checklist do STEP 2
- [ ] App sobe e **não** cria tabela via Hibernate (pois `ddl-auto: none`), e sim via `schema.sql`
- [ ] Tabelas aparecem no H2 Console (`SHOW TABLES;`)
- [ ] Registros do `data.sql` aparecem (`SELECT * FROM ...`)
- [ ] Tipos/constraints conferem com o que será mapeado na **NinjaEntity** (STEP 3)

---

## 6) Erros comuns & soluções
- **`schema.sql` não executa:** verifique `spring.sql.init.mode: always` e se o arquivo está em `src/main/resources`.
- **`data.sql` não carrega:** confirme a ordem (o Spring roda `schema.sql` antes de `data.sql`), conteúdo SQL válido e a mesma localização.
- **Conflito com `ddl-auto`:** deixe `spring.jpa.hibernate.ddl-auto: none` quando usar DDL manual.
- **URL errada no H2 Console:** use exatamente a JDBC URL do `application.yml`.
- **Case sensitivo em nomes:** padronize `snake_case`/maiúsculas e reflita isso na entidade JPA.

---

## 7) Próximo passo
Ir para o **[STEP 3 — Entidade e Repository](README_STEP_3.md)** para mapear a `NinjaEntity` e o `NinjaRepository`, garantindo alinhamento total com o `schema.sql`.
