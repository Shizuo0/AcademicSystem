-- ========================================
-- SCHEMA DO SISTEMA ACADÊMICO
-- Banco de dados para persistência de matrículas e reservas de livros
-- ========================================

-- Criar banco de dados se não existir
CREATE DATABASE IF NOT EXISTS sistema_academico
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Usar o banco de dados
USE sistema_academico;

-- ========================================
-- TABELA: matriculas
-- Armazena as simulações de matrícula de discentes em disciplinas
-- ========================================
CREATE TABLE IF NOT EXISTS matriculas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo_matricula VARCHAR(20) NOT NULL UNIQUE,
    discente_id VARCHAR(50) NOT NULL,
    disciplina_id VARCHAR(50) NOT NULL,
    data_matricula DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraint: um discente não pode se matricular duas vezes na mesma disciplina
    CONSTRAINT uk_matricula_discente_disciplina UNIQUE (discente_id, disciplina_id),
    
    -- Índices para melhorar performance de consultas
    INDEX idx_matricula_codigo (codigo_matricula),
    INDEX idx_matricula_discente (discente_id),
    INDEX idx_matricula_disciplina (disciplina_id),
    INDEX idx_matricula_data (data_matricula)
);

-- ========================================
-- TABELA: reservas_livros
-- Armazena as simulações de reserva de livros por discentes
-- ========================================
CREATE TABLE IF NOT EXISTS reservas_livros (
    id INT AUTO_INCREMENT PRIMARY KEY,
    discente_id VARCHAR(50) NOT NULL,
    livro_id VARCHAR(50) NOT NULL,
    data_reserva DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraint: um discente não pode reservar o mesmo livro duas vezes
    CONSTRAINT uk_reserva_discente_livro UNIQUE (discente_id, livro_id),
    
    -- Índices para melhorar performance de consultas
    INDEX idx_reserva_discente (discente_id),
    INDEX idx_reserva_livro (livro_id),
    INDEX idx_reserva_data (data_reserva)
);

-- ========================================
-- DADOS DE TESTE (OPCIONAL)
-- Descomente as linhas abaixo para inserir dados de teste
-- ========================================

-- INSERT INTO matriculas (discente_id, disciplina_id, data_matricula) VALUES
-- ('1', '1', '2025-01-15'),
-- ('1', '2', '2025-01-15'),
-- ('2', '1', '2025-01-16');

-- INSERT INTO reservas_livros (discente_id, livro_id, data_reserva) VALUES
-- ('1', '1', '2025-01-15'),
-- ('2', '2', '2025-01-16');

-- ========================================
-- CONSULTAS ÚTEIS PARA VERIFICAÇÃO
-- ========================================

-- Listar todas as matrículas
-- SELECT * FROM matriculas ORDER BY created_at DESC;

-- Contar matrículas por discente
-- SELECT discente_id, COUNT(*) as total FROM matriculas GROUP BY discente_id;

-- Contar matrículas por disciplina
-- SELECT disciplina_id, COUNT(*) as total FROM matriculas GROUP BY disciplina_id;

-- Listar todas as reservas
-- SELECT * FROM reservas_livros ORDER BY created_at DESC;

-- Verificar se um livro está reservado
-- SELECT COUNT(*) as reservado FROM reservas_livros WHERE livro_id = '1';

-- ========================================
-- LIMPEZA AUTOMÁTICA
-- ========================================
-- ATENÇÃO: As tabelas são AUTOMATICAMENTE limpas ao encerrar a aplicação!
-- O sistema executa TRUNCATE em todas as tabelas via shutdown hook.
-- Isso garante que os dados não sejam persistidos entre execuções.

-- Para limpar manualmente (se necessário):
-- TRUNCATE TABLE reservas_livros;
-- TRUNCATE TABLE matriculas;
