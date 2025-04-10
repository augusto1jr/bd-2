-- ##### 2. Consultas com DQL: #####

-- a. Listagem de Filmes por Categoria: 
-- Conta quantos filmes existem em cada categoria e ordena da mais populosa para a menos.
SELECT c.name AS categoria, COUNT(fc.film_id) AS n_filmes
FROM category c
JOIN film_category fc ON c.category_id = fc.category_id
GROUP BY c.name
ORDER BY n_filmes DESC;

-- b. Filmes Mais Populares:
-- Lista os 10 filmes com mais locações, ordenados pelo número de vezes alugados.
SELECT f.title AS filme, COUNT(r.rental_id) AS n_locacoes
FROM rental r
JOIN inventory i ON r.inventory_id = i.inventory_id
JOIN film f ON i.film_id = f.film_id
GROUP BY f.title
ORDER BY n_locacoes DESC LIMIT 10;

-- c. Total de Pagamentos por Cliente:  
-- Mostra os clientes que gastaram mais de 100, somando todos os seus pagamentos.
SELECT c.first_name AS nome, c.last_name AS sobrenome, SUM(p.amount) AS total_gasto
FROM customer c
JOIN payment p ON p.customer_id = c.customer_id
GROUP BY c.customer_id
HAVING total_gasto > 100
ORDER BY total_gasto DESC;

-- ##### 3. Transações com DTL: #####  

-- a. Cadastro de Novo Pagamento:  
-- Inicia uma transação e insere um novo pagamento; depois faz rollback, desfazendo a inserção.
START TRANSACTION;
INSERT INTO payment (customer_id, staff_id, rental_id, amount, payment_date) 
VALUES (1, 1, 1, 19.99, NOW());

/*UPDATE customer 
SET notes = 'Pagamento Realizado' 
WHERE customer_id = 1;*/

/* A instrução era para 
adicionar uma anotação no campo notes da tabela Customer,
porém esse campo não existe na tabela*/

ROLLBACK;

-- b. Cancelamento de Pagamento:
-- Inicia uma transação, define um ponto de salvamento, deleta um pagamento, 
-- e depois desfaz a operação usando rollback em caso de erro.
START TRANSACTION;
SAVEPOINT pre_cancelamento;
DELETE FROM payment WHERE payment_id = 1;
COMMIT;
ROLLBACK TO pre_cancelamento;

-- ##### 4. Trabalhando com Views: #####  

-- a. Criar uma View de Clientes VIP:  
-- Cria uma view com clientes que gastaram mais de 200, 
-- permitindo consultas rápidas a esses dados.
CREATE VIEW clientes_vip AS
SELECT c.customer_id, c.first_name, c.last_name, SUM(p.amount) AS total_gasto
FROM customer c
JOIN payment p ON c.customer_id = p.customer_id
GROUP BY c.customer_id
HAVING SUM(p.amount) > 200
ORDER BY total_gasto DESC;

-- b. Consulta na View:  
-- Exibe todos os clientes registrados na view de clientes VIP.
SELECT * FROM clientes_vip;

-- ##### 5. Uso de Funções: #####  

-- a. Análise de Duração de Filmes:  
-- Mostra a média de duração dos filmes por classificação (rating),
-- filtrando apenas as com média acima de 120 minutos.
SELECT rating, AVG(length) AS media_duracao
FROM film
GROUP BY rating
HAVING AVG(length) > 120;

-- b. Relatório de Locações por Data:
-- Conta o número de locações realizadas em cada mês do ano de 2005.
SELECT MONTH(rental_date) AS mes, COUNT(rental_id) AS total_locacoes
FROM rental
WHERE YEAR(rental_date) = 2005
GROUP BY MONTH(rental_date)
ORDER BY mes;

-- ##### 6. Trabalhando com Triggers: #####

-- a. Trigger para Log de Pagamentos:
-- Cria uma tabela de log e uma trigger que 
-- registra automaticamente dados de pagamentos inseridos.
CREATE TABLE payment_log (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    payment_id INT,
    customer_id INT,
    amount DECIMAL(5,2),
    operation VARCHAR(10),
    last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DELIMITER $$
CREATE TRIGGER after_payment_insert
AFTER INSERT ON payment
FOR EACH ROW
BEGIN
 INSERT INTO payment_log (payment_id, customer_id, amount, operation)
 VALUES (NEW.payment_id, NEW.customer_id, NEW.amount, 'INSERT');
END $$
DELIMITER ;

-- Teste Trigger Pagamentos
-- Insere um pagamento e verifica se a trigger registrou no log.
INSERT INTO payment (customer_id, staff_id, rental_id, amount, payment_date)
VALUES (1, 1, 1, 10.00, NOW());
SELECT * FROM payment_log;

-- b. Trigger para Atualização de Endereço:
-- Cria um log de atualizações de endereços e 
-- uma trigger que grava alterações de endereço feitas por clientes.
CREATE TABLE address_log (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    address_id INT,
    customer_id INT,
    operation VARCHAR(10),
    last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DELIMITER $$
CREATE TRIGGER after_address_update
AFTER UPDATE ON address
FOR EACH ROW
BEGIN
    DECLARE v_customer_id INT;
    SELECT customer_id INTO v_customer_id
    FROM customer c
    WHERE c.address_id = NEW.address_id
    LIMIT 1;
    
	INSERT INTO address_log (address_id, customer_id, operation) 
    VALUES (NEW.address_id, v_customer_id, 'UPDATE');
END $$
DELIMITER ;

-- Teste Trigger Endereço
-- Atualiza um endereço e verifica se a trigger registrou no log.
UPDATE address
SET address = 'Street 1', last_update = NOW()
WHERE address_id = 1;
SELECT * FROM address_log;

-- ##### 7. Trabalhando com Stored Procedures: #####

-- a. Criação de uma Stored Procedure para Cadastro de Cliente:
-- Cria uma procedure que cadastra um novo cliente, impedindo duplicidade de e-mails.
DELIMITER $$

CREATE PROCEDURE CadastrarCliente(
	IN p_first_name VARCHAR(20),
    IN p_last_name VARCHAR(20),
    IN p_email VARCHAR(50),
    IN address_id INT)
    
BEGIN
	IF EXISTS (
		SELECT 1 FROM customer WHERE email = p_email
	) THEN
		SIGNAL SQLSTATE '45000'
		SET MESSAGE_TEXT = 'Erro: E-mail já cadastrado.';
        
	ELSE
		INSERT INTO customer(store_id, first_name, last_name, email, address_id, create_date)
		VALUES (1, p_first_name, p_last_name, p_email, address_id, NOW());
	END IF;
END $$

DELIMITER ;

-- Teste de Procedure
-- Tenta cadastrar um cliente, e impede se o e-mail já existir.
CALL CadastrarCliente('Thiago', 'Pinheiro', 'thiago.pinheiro@pe.senac.br', 5);
