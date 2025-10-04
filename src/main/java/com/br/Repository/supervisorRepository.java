package com.br.Repository;

import com.br.Entity.supervisor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface supervisorRepository extends JpaRepository<supervisor, UUID>{
    Optional<supervisor> findByEmail(String email);
}
