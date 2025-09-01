package com.br.Repository;

import com.br.Entity.relatorioTaticoPsicologico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface relatorioTaticoRepository extends JpaRepository<relatorioTaticoPsicologico, Long> {

}