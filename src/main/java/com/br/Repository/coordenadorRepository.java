package com.br.Repository;

import com.br.Entity.coordenador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface coordenadorRepository extends JpaRepository<coordenador, UUID> {
    Optional<coordenador> findByEmail(String email);
}
