package com.br.Repository;

import com.br.Entity.analiseIa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface analiseIaRepository extends JpaRepository<analiseIa, Long> {

    List<analiseIa> findByAtletaEmailOrderByDataAnaliseDesc(String atletaEmail);

}