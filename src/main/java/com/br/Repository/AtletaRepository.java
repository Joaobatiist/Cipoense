package com.br.Repository;

import com.br.Entity.Atleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AtletaRepository extends JpaRepository<Atleta, Long> {
    Optional<Atleta> findByEmail(String email);
}
