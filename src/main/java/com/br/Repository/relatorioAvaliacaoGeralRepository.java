package com.br.Repository;

import com.br.Entity.relatorioAvaliacaoGeral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface relatorioAvaliacaoGeralRepository extends JpaRepository<relatorioAvaliacaoGeral, Long> {
    List<relatorioAvaliacaoGeral> findAllByAtletaIdOrderByDataAvaliacaoAsc(Long atletaId);
    List<relatorioAvaliacaoGeral> findAllByAtletaId(Long atletaId);
}