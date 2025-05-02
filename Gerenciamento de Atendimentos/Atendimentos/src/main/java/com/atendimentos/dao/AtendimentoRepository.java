package com.atendimentos.dao;

import com.atendimentos.model.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    List<Atendimento> findByClienteId(Long clienteId);

    List<Atendimento> findByColaboradorId(Long colaboradorId);
    
    List<Atendimento> findByStatus(Atendimento.Status status);
}
