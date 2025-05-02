-- Criação do Banco de Dados
CREATE DATABASE atendimentos;

USE atendimentos;

-- Criação da tabela Cliente
CREATE TABLE Cliente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    telefone VARCHAR(20)
);

-- Criação da tabela Colaborador
CREATE TABLE Colaborador (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cargo VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL
);

-- Criação da tabela Atendimento
CREATE TABLE Atendimento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    colaborador_id INT NOT NULL,
    data DATETIME NOT NULL,
    descricao TEXT,
    status ENUM('Aberto', 'Andamento', 'Concluido') NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES Cliente(id),
    FOREIGN KEY (colaborador_id) REFERENCES Colaborador(id)
);

-- Inserção de clientes
INSERT INTO Cliente (nome, email, telefone) VALUES
('Ana Souza', 'ana.souza@example.com', '(11) 98765-4321'),
('Bruno Lima', 'bruno.lima@example.com', '(11) 97654-3210'),
('Carla Mendes', 'carla.mendes@example.com', '(11) 96543-2109'),
('Daniel Rocha', 'daniel.rocha@example.com', '(11) 95432-1098'),
('Eduarda Silva', 'eduarda.silva@example.com', '(11) 94321-0987');

-- Inserção de colaboradores
INSERT INTO Colaborador (nome, cargo, email) VALUES
('Fábio Pereira', 'Atendente', 'fabio.pereira@example.com'),
('Gisele Costa', 'Supervisor', 'gisele.costa@example.com'),
('Henrique Almeida', 'Atendente', 'henrique.almeida@example.com'),
('Isabela Ramos', 'Gerente', 'isabela.ramos@example.com'),
('João Martins', 'Atendente', 'joao.martins@example.com');

-- Inserção de atendimentos
INSERT INTO Atendimento (cliente_id, colaborador_id, data, descricao, status) VALUES
(1, 1, '2025-04-25 10:00:00', 'Solicitação de informações sobre produto.', 'Aberto'),
(2, 3, '2025-04-25 11:30:00', 'Reclamação de atraso na entrega.', 'Andamento'),
(3, 2, '2025-04-25 14:00:00', 'Pedido de reembolso.', 'Concluido'),
(4, 5, '2025-04-26 09:00:00', 'Dúvidas sobre formas de pagamento.', 'Aberto'),
(5, 4, '2025-04-26 15:30:00', 'Feedback positivo sobre atendimento.', 'Concluido');
