package com.br.Repository;

import com.br.Entity.estoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface estoqueRepository extends JpaRepository<estoque, UUID> {

}
