package com.pedidos.dao;

import com.pedidos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    // Busca pedidos por cliente
    List<Pedido> findByClienteId(Long clienteId);
    
    // Busca pedidos por data
    List<Pedido> findByDataPedido(LocalDate dataPedido);
    
    // Ordena pedidos por data (mais recente primeiro)
    List<Pedido> findAllByOrderByDataPedidoDesc();
    
    // Ordena pedidos por valor (maior primeiro)
    List<Pedido> findAllByOrderByValorTotalDesc();
}