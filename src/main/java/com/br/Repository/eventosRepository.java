package com.br.Repository;

import com.br.Entity.eventos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface eventosRepository extends JpaRepository<eventos, UUID> {
}
