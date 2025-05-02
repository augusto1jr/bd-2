package com.pedidos.service;

import com.pedidos.dao.PedidoRepository;
import com.pedidos.model.Cliente;
import com.pedidos.model.Pedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {
    
    @Autowired
    private PedidoRepository pedidoRepository;
    
    public Pedido salvar(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }
    
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }
    
    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }
    
    public List<Pedido> buscarPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }
    
    public List<Pedido> buscarPorData(LocalDate dataPedido) {
        return pedidoRepository.findByDataPedido(dataPedido);
    }
    
    public List<Pedido> listarRecentes() {
        return pedidoRepository.findAllByOrderByDataPedidoDesc();
    }
    
    public List<Pedido> listarPorValorDesc() {
        return pedidoRepository.findAllByOrderByValorTotalDesc();
    }
    
    public Pedido atualizar(Long id, Pedido pedidoAtualizado) {
        return pedidoRepository.findById(id)
            .map(pedido -> {
                pedido.setCliente(pedidoAtualizado.getCliente());
                pedido.setDataPedido(pedidoAtualizado.getDataPedido());
                pedido.setValorTotal(pedidoAtualizado.getValorTotal());
                return pedidoRepository.save(pedido);
            })
            .orElseThrow(() -> new RuntimeException("Pedido não encontrado com o ID: " + id));
    }
    
    public void excluir(Long id) {
        pedidoRepository.deleteById(id);
    }
}