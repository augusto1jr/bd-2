package com.atendimentos.service;

import com.atendimentos.dao.ColaboradorRepository;
import com.atendimentos.model.Colaborador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ColaboradorService {

    @Autowired
    private ColaboradorRepository colaboradorRepository;

    public Colaborador salvar(Colaborador colaborador) {
        return colaboradorRepository.save(colaborador);
    }

    public List<Colaborador> listarTodos() {
        return colaboradorRepository.findAll();
    }

    public Optional<Colaborador> buscarPorId(Long id) {
        return colaboradorRepository.findById(id);
    }

    public Colaborador buscarPorEmail(String email) {
        return colaboradorRepository.findByEmail(email);
    }

    public Colaborador atualizar(Long id, Colaborador colaboradorAtualizado) {
        return colaboradorRepository.findById(id)
            .map(colaborador -> {
                colaborador.setNome(colaboradorAtualizado.getNome());
                colaborador.setCargo(colaboradorAtualizado.getCargo());
                colaborador.setEmail(colaboradorAtualizado.getEmail());
                return colaboradorRepository.save(colaborador);
            })
            .orElseThrow(() -> new RuntimeException("Colaborador não encontrado com o ID: " + id));
    }

    public void excluir(Long id) {
        colaboradorRepository.deleteById(id);
    }

    public boolean emailExiste(String email, Long idExcluido) {
        if (idExcluido == null) {
            return colaboradorRepository.existsByEmail(email);
        }
        return colaboradorRepository.existsByEmailAndIdNot(email, idExcluido);
    }
}
