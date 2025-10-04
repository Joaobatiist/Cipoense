package com.br.Repository;

import com.br.Entity.relatorioDesempenho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface relatorioDesempenhoRepository extends JpaRepository<relatorioDesempenho, UUID> {

}