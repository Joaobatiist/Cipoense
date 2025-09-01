package com.br.Repository;

import com.br.Entity.supervisor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface supervisorRepository extends JpaRepository<supervisor, Long>{
    Optional<supervisor> findByEmail(String email);
}
