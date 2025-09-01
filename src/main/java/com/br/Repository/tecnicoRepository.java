package com.br.Repository;


import com.br.Entity.tecnico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface tecnicoRepository extends JpaRepository <tecnico, Long>{
    Optional<tecnico> findByEmail(String email);
}
