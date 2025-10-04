package com.br.Repository;

import com.br.Entity.relatorioAvaliacaoGeral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface relatorioAvaliacaoGeralRepository extends JpaRepository<relatorioAvaliacaoGeral, UUID> {
    List<relatorioAvaliacaoGeral> findAllByAtletaIdOrderByDataAvaliacaoAsc(UUID atletaId);
    List<relatorioAvaliacaoGeral> findAllByAtletaId(UUID atletaId);
}