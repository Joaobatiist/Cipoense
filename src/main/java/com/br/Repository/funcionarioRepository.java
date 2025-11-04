package com.br.Repository;

import com.br.Entity.funcionario;
import com.br.Enums.role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface funcionarioRepository extends JpaRepository<funcionario, UUID> {
    Optional<funcionario> findByEmail(String email);
    Optional<funcionario> findAllByRole(role role);


    void deleteById(UUID id);
}
