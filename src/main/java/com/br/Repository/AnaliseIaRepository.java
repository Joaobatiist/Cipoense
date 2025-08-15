package com.br.Repository;

import com.br.Entity.AnaliseIa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnaliseIaRepository extends JpaRepository<AnaliseIa, Long> {

    List<AnaliseIa> findByAtletaEmailOrderByDataAnaliseDesc(String atletaEmail);

}