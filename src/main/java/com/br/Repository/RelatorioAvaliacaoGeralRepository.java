package com.br.Repository;

import com.br.Entity.RelatorioAvaliacaoGeral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelatorioAvaliacaoGeralRepository extends JpaRepository<RelatorioAvaliacaoGeral, Long> {

}