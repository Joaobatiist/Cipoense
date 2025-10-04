package com.br.Repository;

import com.br.Entity.responsavel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface responsavelRepository extends JpaRepository<responsavel, UUID> {
}
