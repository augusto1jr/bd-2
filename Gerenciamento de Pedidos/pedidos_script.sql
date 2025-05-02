-- Criação do Banco de Dados
CREATE DATABASE pedidos;

USE pedidos;

-- Criação da tabela de clientes 
CREATE TABLE clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    fone VARCHAR(20), 
    login VARCHAR(50) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL
);

-- Criação da tabela de pedidos
CREATE TABLE pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    data_pedido DATE NOT NULL,
    valor_total DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_cliente) REFERENCES clientes(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- Inserção de clientes 
INSERT INTO clientes (nome, fone, login, senha) VALUES
('Ana Silva', '(11) 98765-4321', 'ana.silva', 'senha123'),
('Bruno Costa', '(21) 99876-5432', 'bruno.costa', 'senha456'),
('Carla Mendes', '(31) 98765-1234', 'carla.mendes', 'senha789'),
('Diego Rocha', '(41) 91234-5678', 'diego.rocha', 'senha321'),
('Elisa Souza', '(51) 99345-6789', 'elisa.souza', 'senha654');

-- Inserção de pedidos 
INSERT INTO pedidos (id_cliente, data_pedido, valor_total) VALUES
(1, '2025-04-01', 150.00),
(1, '2025-04-05', 220.50),
(1, '2025-04-10', 85.90),
(2, '2025-04-03', 199.99),
(2, '2025-04-06', 349.75),
(2, '2025-04-12', 120.00),
(2, '2025-04-15', 89.40),
(3, '2025-04-02', 70.00),
(3, '2025-04-08', 130.60),
(3, '2025-04-13', 99.99),
(4, '2025-04-01', 250.00),
(4, '2025-04-04', 175.75),
(4, '2025-04-11', 300.00),
(4, '2025-04-14', 50.25),
(4, '2025-04-16', 215.90),
(5, '2025-04-07', 199.00),
(5, '2025-04-09', 143.70),
(5, '2025-04-13', 184.30),
(5, '2025-04-17', 230.00);