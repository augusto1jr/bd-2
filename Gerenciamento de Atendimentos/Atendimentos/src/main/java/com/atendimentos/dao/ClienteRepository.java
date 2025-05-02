package com.atendimentos.dao;

import com.atendimentos.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    Cliente findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    boolean existsByEmailAndIdNot(String email, Long id);
    
    boolean existsByTelefone(String telefone);
    
    boolean existsByTelefoneAndIdNot(String telefone, Long id);
}
