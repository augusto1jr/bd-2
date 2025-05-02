package com.pedidos.service;

import com.pedidos.dao.ClienteRepository;
import com.pedidos.model.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    public Cliente salvar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }
    
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }
    
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }
    
    public Cliente buscarPorLogin(String login) {
        return clienteRepository.findByLogin(login);
    }    
    
    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        return clienteRepository.findById(id)
            .map(cliente -> {
                cliente.setNome(clienteAtualizado.getNome());
                cliente.setTelefone(clienteAtualizado.getTelefone());
                cliente.setLogin(clienteAtualizado.getLogin());
                cliente.setSenha(clienteAtualizado.getSenha());
                return clienteRepository.save(cliente);
            })
            .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o ID: " + id));
    }
    
    public void excluir(Long id) {
        clienteRepository.deleteById(id);
    }
    
    public boolean loginExists(String login, Long excludedId) {
        if (excludedId == null) {
            return clienteRepository.existsByLogin(login);
        }
        return clienteRepository.existsByLoginAndIdNot(login, excludedId);
    }

    public boolean telefoneExists(String telefone, Long excludedId) {
        if (telefone == null || telefone.isBlank()) {
            return false;
        }
        if (excludedId == null) {
            return clienteRepository.existsByTelefone(telefone);
        }
        return clienteRepository.existsByTelefoneAndIdNot(telefone, excludedId);
    }
}