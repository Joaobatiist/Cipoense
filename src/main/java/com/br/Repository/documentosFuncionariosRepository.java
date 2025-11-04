package com.br.Repository;

import com.br.Entity.documentosFuncionarios;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface documentosFuncionariosRepository extends JpaRepository<documentosFuncionarios, UUID> {
}
