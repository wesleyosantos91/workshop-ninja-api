# Passo 2 - Banco de Dados (H2 + Tabelas + Dados)

## O que vamos fazer
Vamos criar a tabela NINJA no banco H2 e inserir alguns dados de exemplo.

## 1) Como funciona o H2 Database

O H2 é um banco de dados que roda na memória do computador:
- É rápido e fácil de usar para estudos
- Os dados são perdidos quando você para a aplicação
- Perfeito para desenvolvimento e testes

## 2) Configuração no application.yml

O arquivo `application.yml` já está configurado para trabalhar com H2 e executar nossos scripts SQL:

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

**Configurações importantes:**
- `jdbc:h2:mem:naruto` - Banco H2 em memória com nome "naruto"
- `console.enabled: true` - Habilita o console H2 em `/h2`
- `ddl-auto: none` - Não gera tabelas automaticamente (usaremos schema.sql)
- `sql.init.mode: always` - **SEMPRE executa os arquivos schema.sql e data.sql**

Essa última configuração é fundamental! Ela garante que:
1. O `schema.sql` seja executado primeiro (criando as tabelas)
2. O `data.sql` seja executado depois (inserindo os dados)

## 3) Criando a tabela (schema.sql)

O arquivo `src/main/resources/schema.sql` define a estrutura da nossa tabela:

```sql
CREATE TABLE IF NOT EXISTS NINJA (
    id_ninja       INT AUTO_INCREMENT PRIMARY KEY,  -- ID único
    nome           VARCHAR(100) NOT NULL,           -- Nome do ninja
    vila           VARCHAR(50) NOT NULL,            -- Vila de origem
    cla            VARCHAR(50),                     -- Clã
    rank           VARCHAR(20) NOT NULL,            -- Genin, Chunin, Jounin, Kage
    chakra_tipo    VARCHAR(30) NOT NULL,            -- Tipo de chakra
    especialidade  VARCHAR(50),                     -- Especialidade
    kekkei_genkai  VARCHAR(50),                     -- Poder especial
    status         VARCHAR(20) DEFAULT 'Ativo',     -- Status atual
    nivel_forca    INT,                             -- Nível de força (1-100)
    data_registro  DATE DEFAULT CURRENT_DATE,       -- Data de registro
    CONSTRAINT chk_nivel_forca CHECK (nivel_forca BETWEEN 1 AND 100)
);
```

**Entenda a tabela:**
- `AUTO_INCREMENT` - o ID aumenta automaticamente
- `NOT NULL` - campos obrigatórios
- `DEFAULT` - valor padrão se não informado
- `CHECK` - valida se nível de força está entre 1 e 100

## 4) Inserindo dados de exemplo (data.sql)

O arquivo `src/main/resources/data.sql` insere nossos ninjas iniciais:

```sql
INSERT INTO NINJA (nome, vila, cla, rank, chakra_tipo, especialidade, kekkei_genkai, status, nivel_forca)
VALUES
    ('Naruto Uzumaki', 'Konoha', 'Uzumaki', 'Hokage', 'Vento', 'Ninjutsu', 'Kurama (Bijuu)', 'Ativo', 98),
    ('Sasuke Uchiha', 'Konoha', 'Uchiha', 'Jounin', 'Fogo', 'Ninjutsu/Genjutsu', 'Sharingan/Rinnegan', 'Renegado', 97),
    ('Sakura Haruno', 'Konoha', NULL, 'Jounin', 'Terra', 'Taijutsu/Medicina', NULL, 'Ativo', 85),
    ('Gaara', 'Sunagakure', NULL, 'Kazekage', 'Vento', 'Ninjutsu (Areia)', 'Shukaku (Bijuu)', 'Ativo', 95);
```

**Dados interessantes:**
- **Naruto** - Hokage com nível 98 e a Kurama
- **Sasuke** - Renegado com Sharingan e Rinnegan
- **Sakura** - Especialista em medicina e taijutsu
- **Gaara** - Kazekage com poder da areia e Shukaku

## 5) Testando o banco

1. Execute a aplicação: `./mvnw spring-boot:run`
2. Acesse o H2 Console: http://localhost:8080/h2
3. Faça login:
   - **URL JDBC:** `jdbc:h2:mem:naruto;MODE=MYSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
   - **Username:** `sa` 
   - **Password:** (deixe vazio)
4. Execute no console: `SELECT * FROM NINJA;`
5. Você deve ver todos os 4 ninjas cadastrados!

## Próximo passo
Agora vamos criar a classe Java que representa um ninja (Entity). **[STEP 3 — Entidade e Repository](README_STEP_3.md)**
