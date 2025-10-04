package com.br.Repository;

import com.br.Entity.analiseIa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface analiseIaRepository extends JpaRepository<analiseIa, UUID> {

    List<analiseIa> findByAtletaEmailOrderByDataAnaliseDesc(String atletaEmail);

}