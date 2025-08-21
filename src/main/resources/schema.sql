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