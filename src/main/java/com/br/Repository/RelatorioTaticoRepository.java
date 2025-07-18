package com.br.Repository;

import com.br.Entity.RelatorioTaticoPsicologico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RelatorioTaticoRepository extends JpaRepository<RelatorioTaticoPsicologico, Long> {

}