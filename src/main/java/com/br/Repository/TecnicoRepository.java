package com.br.Repository;


import com.br.Entity.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TecnicoRepository extends JpaRepository <Tecnico, Long>{
    Optional<Tecnico> findByEmail(String email);
}
