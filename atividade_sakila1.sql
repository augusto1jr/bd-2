-- Atividade 1 --
SELECT c.customer_id, c.first_name, c.last_name, COUNT(r.rental_id) AS n_alugueis
FROM customer c
JOIN rental r ON c.customer_id = r.customer_id
GROUP BY c.customer_id
HAVING COUNT(r.rental_id) > 20
ORDER BY n_alugueis DESC;


-- Atividade 2 --
SELECT s.staff_id, s.first_name, s.last_name, SUM(p.amount) AS total_vendas
FROM staff s
JOIN payment p ON s.staff_id = p.staff_id
GROUP BY s.staff_id
HAVING SUM(p.amount) > 5000
ORDER BY total_vendas DESC;


-- Atividade 3 --
SELECT rating, AVG(length) AS media_duracao
FROM film
GROUP BY rating
HAVING AVG(length) > 120;


-- Atividade 4 --
CREATE VIEW clientes_150plus AS
SELECT c.customer_id, c.first_name, c.last_name, SUM(p.amount) AS total_gasto
FROM customer c
JOIN payment p ON c.customer_id = p.customer_id
GROUP BY c.customer_id
HAVING SUM(p.amount) > 150
ORDER BY total_gasto DESC;

SELECT * FROM clientes_150plus;


-- Atividade 5 --
CREATE TEMPORARY TABLE temp_filmes_populares (
 film_id INT,
 titulo VARCHAR(255),
 total_alugueis INT);
 
INSERT INTO temp_filmes_populares
SELECT f.film_id, f.title, COUNT(r.rental_id) AS total_alugueis
FROM film f
JOIN inventory i ON f.film_id = i.film_id
JOIN rental r ON i.inventory_id = r.inventory_id
GROUP BY f.film_id
HAVING total_alugueis > 30
ORDER BY total_alugueis;

SELECT * FROM temp_filmes_populares;


-- Atividade 6 --
START TRANSACTION;

INSERT INTO payment (customer_id, staff_id, rental_id, amount, payment_date)
VALUES (1, 1, NULL, 50.00, NOW());

UPDATE address
SET bairro = 'New York' 
WHERE address_id = (SELECT address_id FROM customer WHERE customer_id = 1);

ROLLBACK


-- Atividade 7 --
DELIMITER $$
CREATE PROCEDURE RegistrarPagamentoSeguro(
 IN p_customer_id INT,
 IN p_staff_id INT,
 IN p_rental_id INT,
 IN p_amount DECIMAL(10,2)
)
BEGIN
 DECLARE EXIT HANDLER FOR SQLEXCEPTION
 BEGIN
 ROLLBACK;
 SELECT 'Erro detectado. Transação revertida.' AS status;
 END;
 START TRANSACTION;
 INSERT INTO payment (customer_id, staff_id, rental_id, amount, payment_date)
 VALUES (p_customer_id, p_staff_id, p_rental_id, p_amount, NOW());
 COMMIT;
 SELECT 'Pagamento registrado com sucesso!' AS status;
END $$
DELIMITER ;


-- Atividade 8 --
CREATE TABLE log_novos_clientes (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    nome VARCHAR(50),
    data_adicao DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);

DELIMITER $$

CREATE TRIGGER after_customer_insert
AFTER INSERT ON customer
FOR EACH ROW
BEGIN
    INSERT INTO log_novos_clientes (customer_id, nome, data_adicao)
    VALUES (NEW.customer_id, CONCAT(NEW.first_name, ' ', NEW.last_name), NOW());
END $$

DELIMITER ;
