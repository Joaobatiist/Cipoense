package com.br.Repository;

import com.br.Entity.RelatorioDesempenho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelatorioDesempenhoRepository extends JpaRepository<RelatorioDesempenho, Long> {

}