-- Inserção de clientes
INSERT INTO clientes (nome, fone, email, senha) VALUES
('Ana Silva', '(11) 98765-4321', 'ana.silva@email.com', 'senha123'),
('Bruno Costa', '(21) 99876-5432', 'bruno.costa@email.com', 'senha456'),
('Carla Mendes', '(31) 98765-1234', 'carla.mendes@email.com', 'senha789'),
('Diego Rocha', '(41) 91234-5678', 'diego.rocha@email.com', 'senha321'),
('Elisa Souza', '(51) 99345-6789', 'elisa.souza@email.com', 'senha654');

-- Inserção de pedidos para Ana Silva (id = 1)
INSERT INTO pedidos (id_cliente, data_pedido, valor_total) VALUES
(1, '2025-04-01', 150.00),
(1, '2025-04-05', 220.50),
(1, '2025-04-10', 85.90);

-- Inserção de pedidos para Bruno Costa (id = 2)
INSERT INTO pedidos (id_cliente, data_pedido, valor_total) VALUES
(2, '2025-04-03', 199.99),
(2, '2025-04-06', 349.75),
(2, '2025-04-12', 120.00),
(2, '2025-04-15', 89.40);

-- Inserção de pedidos para Carla Mendes (id = 3)
INSERT INTO pedidos (id_cliente, data_pedido, valor_total) VALUES
(3, '2025-04-02', 70.00),
(3, '2025-04-08', 130.60),
(3, '2025-04-13', 99.99);

-- Inserção de pedidos para Diego Rocha (id = 4)
INSERT INTO pedidos (id_cliente, data_pedido, valor_total) VALUES
(4, '2025-04-01', 250.00),
(4, '2025-04-04', 175.75),
(4, '2025-04-11', 300.00),
(4, '2025-04-14', 50.25),
(4, '2025-04-16', 215.90);

-- Inserção de pedidos para Elisa Souza (id = 5)
INSERT INTO pedidos (id_cliente, data_pedido, valor_total) VALUES
(5, '2025-04-07', 199.00),
(5, '2025-04-09', 143.70),
(5, '2025-04-13', 184.30),
(5, '2025-04-17', 230.00);
