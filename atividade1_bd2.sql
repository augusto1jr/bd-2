-- ############ 1. DDL ############ --

-- 1. Criação e Modificação de Estruturas --

-- 1.1.1 Crie um banco de dados chamado 'universidade' e selecione-o para uso. --
CREATE DATABASE universidade
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;
 
USE universidade;

-- 1.1.2 Criando tabela professores --
CREATE TABLE professores(
	id_professor INT PRIMARY KEY AUTO_INCREMENT,
    nome_professor VARCHAR(100) NOT NULL,
    departamento VARCHAR(100),
    data_contratacao DATE
)DEFAULT CHARACTER SET utf8mb4;
 
-- 1.1.3 Modificando tabela professores --
ALTER TABLE professores
	ADD COLUMN salario DECIMAL(10,2),
    -- 1.1.4 Remova a coluna 'departamento' da tabela 'professores'. --
    DROP COLUMN departamento;
 
-- 1.1.5 Criando tabela disciplinas --
CREATE TABLE disciplinas(
	id_disciplina INT PRIMARY KEY AUTO_INCREMENT,
    nome_disciplina VARCHAR(100),
    carga_horaria INT,
    id_professor INT, 
    FOREIGN KEY (id_professor) REFERENCES professores(id_professor)
)DEFAULT CHARACTER SET utf8mb4;
 
-- 1.1.6 Altere a tabela 'disciplinas' para adicionar uma coluna 'descricao' --
ALTER TABLE disciplinas
	ADD COLUMN descricao VARCHAR(255);

-- 1.1.7. Exclua a tabela 'disciplinas'. --
DROP TABLE disciplinas;
 
 
-- ############ 2. DML ############ --
 
-- 2.2 Inserção de Dados --
CREATE TABLE departamentos(
	id_departamento INT PRIMARY KEY AUTO_INCREMENT,
    nome_departamento VARCHAR(100),
    bloco_departamento VARCHAR(10),
    data_criacao DATE
);
 
-- 2.2.1 Inserção de dados na tabela 'departamentos' --
INSERT INTO departamentos(nome_departamento, bloco_departamento, data_criacao) VALUES
('Engenharia', 'Bloco E', '2005-03-15'),
('Administração', 'Bloco F', '2010-06-20'),
('Tecnologia da Informação', 'Bloco G', '2015-09-10'),
('Psicologia', 'Bloco H', '2012-11-25');

-- Criando tabela empregados --
CREATE TABLE empregados(
	id_empregado INT PRIMARY KEY AUTO_INCREMENT,
    nome_empregado VARCHAR(100) NOT NULL,
    id_departamento INT NOT NULL,
    data_contratacao DATE,
    salario INT,
    FOREIGN KEY (id_departamento) REFERENCES departamentos(id_departamento) ON DELETE CASCADE
) DEFAULT CHARACTER SET utf8mb4;

-- 2.2.2 Adicione um novo empregado chamado "Lucas Fernandes" --
INSERT INTO empregados(nome_empregado, id_departamento, data_contratacao, salario) VALUES
('Lucas Fernandes', 3, '2022-09-01', 7500),
('Carlos Júnior', 1, '2021-08-02', 5000),
('Mariana Barbosa', 4, '2020-06-04', 4000),
('Mariana Mendonça', 4, '2018-07-09', 3000);

-- Criando tabela editora --
CREATE TABLE editora (
    id_editora INT PRIMARY KEY AUTO_INCREMENT,
    nome_editora VARCHAR(100) NOT NULL,
    cidade_editora VARCHAR(100) NOT NULL
) DEFAULT CHARACTER SET utf8mb4;

-- Inserindo registros na tabela editora --
INSERT INTO editora (nome_editora, cidade_editora) VALUES 
('Editora Novatec', 'São Paulo'), 
('Editora Intrinseca', 'Rio de Janeiro'), 
('Editora Saber', 'Belo Horizonte');

-- Criando tabela biblioteca --
CREATE TABLE biblioteca (
    id_livro INT PRIMARY KEY AUTO_INCREMENT,
    nome_livro VARCHAR(100) NOT NULL,
    id_editora INT NOT NULL,
    data_publicacao YEAR,
    isbn VARCHAR(13),
    FOREIGN KEY (id_editora) REFERENCES editora(id_editora) ON DELETE CASCADE
) DEFAULT CHARACTER SET utf8mb4;

-- Inserindo registros na tabela biblioteca --
INSERT INTO biblioteca (nome_livro, id_editora, data_publicacao, isbn) VALUES
('Banco de Dados Intermediário', 1, 2022, '4567891230123');

-- Criando tabela alunos --
CREATE TABLE alunos (
    id_aluno INT PRIMARY KEY AUTO_INCREMENT,
    nome_aluno VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL
) DEFAULT CHARACTER SET utf8mb4;

-- Inserindo registros na tabela alunos --
INSERT INTO alunos (nome_aluno, email) VALUES 
('Ana Oliveira', 'ana.oliveira@email.com'),
('Bruno Costa', 'bruno.costa@email.com'),
('Carlos Silva', 'carlos.silva@email.com');

-- Criando tabela autores --
CREATE TABLE autores (
    id_autor INT PRIMARY KEY AUTO_INCREMENT,
    nome_autor VARCHAR(100) NOT NULL,
    nacionalidade VARCHAR(100) NOT NULL
) DEFAULT CHARACTER SET utf8mb4;

-- Inserindo registros na tabela autores --
INSERT INTO autores (nome_autor, nacionalidade) VALUES 
('João Pereira', 'Brasileiro'),
('Maria Souza', 'Brasileira'),
('Carlos Almeida', 'Português');

-- Criando tabela livros_autores (associando livros aos autores) --
CREATE TABLE livros_autores (
    id_livro INT,
    id_autor INT,
    PRIMARY KEY (id_livro, id_autor),
    FOREIGN KEY (id_livro) REFERENCES biblioteca(id_livro),
    FOREIGN KEY (id_autor) REFERENCES autores(id_autor)
);

-- Associando autores aos livros --
INSERT INTO livros_autores (id_livro, id_autor) VALUES
(1, 1), -- João Pereira escreveu "Banco de Dados Intermediário"
(1, 2); -- Maria Souza escreveu "Banco de Dados Intermediário"

-- Criando tabela aluguel para representar o empréstimo de livros pelos alunos --
CREATE TABLE aluguel (
    id_aluguel INT PRIMARY KEY AUTO_INCREMENT,
    id_aluno INT NOT NULL,
    id_livro INT NOT NULL,
    data_emprestimo DATE NOT NULL,
    data_devolucao DATE,
    FOREIGN KEY (id_aluno) REFERENCES alunos(id_aluno) ON DELETE CASCADE,
    FOREIGN KEY (id_livro) REFERENCES biblioteca(id_livro) ON DELETE CASCADE
) DEFAULT CHARACTER SET utf8mb4;

-- Inserindo registros na tabela aluguel --
INSERT INTO aluguel (id_aluno, id_livro, data_emprestimo, data_devolucao) VALUES 
(1, 1, '2025-02-01', '2025-02-10'),
(2, 1, '2025-02-05', '2025-02-15'),
(3, 1, '2025-02-08', NULL);

 -- 2.2.3 Inserindo dados na tabela 'biblioteca' --
INSERT INTO biblioteca(nome_livro, id_editora, data_publicacao, isbn) VALUES
('Banco de Dados Avançado', 1, '2021', '123456789'),
('Banco de Dados Básico', 1, '2014', '987654321');
 

 -- 2.3 Atualização e Exclusão de Dados
 
 -- 2.3.1 Atualize o salário do empregado "Carlos Júnior" para 5500.00. --
UPDATE empregados SET salario = 5500 WHERE id_empregado = 2;

 -- 2.3.2 Atualize a cidade da editora "Intrinseca" para "Brasília". --
 UPDATE editora SET cidade_editora = 'Brasília' WHERE id_editora = 2;
 
 -- 2.3.3 Remova o empregado cujo nome seja "Mariana Barbosa".
DELETE FROM empregados WHERE id_empregado = 3;

-- 2.3.4 Remova todos os livros da tabela 'biblioteca' que foram publicados antes de 2015.
DELETE FROM biblioteca WHERE data_publicacao < 2015;


-- 4. Consultas

-- 4.1 Liste todos os empregados ordenados por salário de forma decrescente. --
SELECT * FROM empregados ORDER BY nome_empregado DESC;

-- 4.2 Recupere todos os empregados cujo salário seja maior que 5000.00 e que tenham sido admitidos antes de 2020. --
SELECT * FROM empregados WHERE salario < 5000 AND data_contratacao < '2020-01-01';

-- 4.3 Liste todos os departamentos e a quantidade de empregados em cada um (use 'GROUP BY'). --
SELECT nome_departamento, COUNT(e.id_empregado) AS quantidade_empregados
FROM departamentos d 
JOIN empregados e ON d.id_departamento = e.id_departamento
GROUP BY d.id_departamento;

-- 4.4 Exiba os nomes dos departamentos e o total de salários pagos por cada um. --
SELECT d.nome_departamento, SUM(e.salario) AS total_salarios
FROM departamentos d
JOIN empregados e ON d.id_departamento = e.id_departamento
GROUP BY d.id_departamento, d.nome_departamento;

-- 4.5 Encontre o maior salário entre os empregados. --
SELECT MAX(salario) AS maior_salario FROM empregados;

-- 4.6 Liste todos os livros e suas respectivas editoras (use 'JOIN' entre 'Livro' e 'Editora'). --
SELECT l.nome_livro, e.nome_editora 
FROM biblioteca l
JOIN editora e ON l.id_editora = e.id_editora;

-- 4.7 Liste todos os alunos que pegaram livros emprestados e a data de devolução desses empréstimos. --
SELECT a.nome_aluno, al.data_devolucao 
FROM alunos a
JOIN aluguel al ON a.id_aluno = al.id_aluno;

-- 4.8 Liste os livros emprestados e o nome dos alunos que pegaram esses livros. --
SELECT l.nome_livro, a.nome_aluno 
FROM biblioteca l
JOIN aluguel al ON l.id_livro = al.id_livro
JOIN alunos a ON al.id_aluno = a.id_aluno;

-- 4.9 Exiba a média salarial dos empregados por departamento. --
SELECT d.nome_departamento, AVG(e.salario) AS media_salarial
FROM departamentos d
LEFT JOIN empregados e ON d.id_departamento = e.id_departamento
GROUP BY d.id_departamento, d.nome_departamento;

-- 4.10 Encontre os nomes dos autores que escreveram livros publicados pela editora Novatec. --
SELECT a.nome_autor 
FROM autores a
JOIN livros_autores la ON a.id_autor = la.id_autor
JOIN biblioteca l ON la.id_livro = l.id_livro
JOIN editora e ON l.id_editora = e.id_editora
WHERE e.nome_editora = 'Novatec';
