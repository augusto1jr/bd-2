package com.atendimentos.service;

import com.atendimentos.dao.ClienteRepository;
import com.atendimentos.model.Cliente;
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

    public Cliente buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        return clienteRepository.findById(id)
            .map(cliente -> {
                cliente.setNome(clienteAtualizado.getNome());
                cliente.setTelefone(clienteAtualizado.getTelefone());
                cliente.setEmail(clienteAtualizado.getEmail());
                return clienteRepository.save(cliente);
            })
            .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o ID: " + id));
    }

    public void excluir(Long id) {
        clienteRepository.deleteById(id);
    }

    public boolean emailExiste(String email, Long idExcluido) {
        if (idExcluido == null) {
            return clienteRepository.existsByEmail(email);
        }
        return clienteRepository.existsByEmailAndIdNot(email, idExcluido);
    }

    public boolean telefoneExiste(String telefone, Long idExcluido) {
        if (telefone == null || telefone.isBlank()) {
            return false;
        }
        if (idExcluido == null) {
            return clienteRepository.existsByTelefone(telefone);
        }
        return clienteRepository.existsByTelefoneAndIdNot(telefone, idExcluido);
    }
}
