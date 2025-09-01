package com.br.Repository;

import com.br.Entity.atleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface atletaRepository extends JpaRepository<atleta, Long> {
    Optional<atleta> findByEmail(String email);
}
