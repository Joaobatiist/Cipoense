package com.br.Repository;

import com.br.Entity.Supervisor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupervisorRepository extends JpaRepository<Supervisor, Long>{
    Optional<Supervisor> findByEmail(String email);
}
