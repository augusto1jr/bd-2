package com.atendimentos.service;

import com.atendimentos.dao.AtendimentoRepository;
import com.atendimentos.model.Atendimento;
import com.atendimentos.model.Atendimento.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AtendimentoService {

    @Autowired
    private AtendimentoRepository atendimentoRepository;

    public Atendimento salvar(Atendimento atendimento) {
        return atendimentoRepository.save(atendimento);
    }

    public List<Atendimento> listarTodos() {
        return atendimentoRepository.findAll();
    }

    public Optional<Atendimento> buscarPorId(Long id) {
        return atendimentoRepository.findById(id);
    }

    public List<Atendimento> listarPorCliente(Long clienteId) {
        return atendimentoRepository.findByClienteId(clienteId);
    }

    public List<Atendimento> listarPorColaborador(Long colaboradorId) {
        return atendimentoRepository.findByColaboradorId(colaboradorId);
    }

    public List<Atendimento> listarPorStatus(Status status) {
        return atendimentoRepository.findByStatus(status);
    }

    public Atendimento atualizar(Long id, Atendimento atualizado) {
        return atendimentoRepository.findById(id)
            .map(atendimento -> {
                atendimento.setCliente(atualizado.getCliente());
                atendimento.setColaborador(atualizado.getColaborador());
                atendimento.setData(atualizado.getData());
                atendimento.setDescricao(atualizado.getDescricao());
                atendimento.setStatus(atualizado.getStatus());
                return atendimentoRepository.save(atendimento);
            })
            .orElseThrow(() -> new RuntimeException("Atendimento não encontrado com o ID: " + id));
    }

    public void excluir(Long id) {
        atendimentoRepository.deleteById(id);
    }
}
