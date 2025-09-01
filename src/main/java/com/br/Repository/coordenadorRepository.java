package com.br.Repository;

import com.br.Entity.coordenador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface coordenadorRepository extends JpaRepository<coordenador, Long> {
    Optional<coordenador> findByEmail(String email);
}
