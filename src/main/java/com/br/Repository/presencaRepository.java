package com.br.Repository;

import com.br.Entity.atleta;
import com.br.Entity.eventos;
import com.br.Entity.presenca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface presencaRepository extends JpaRepository <presenca, UUID>{
    void deleteByAtleta(atleta atleta);


    List<presenca> findByData(LocalDate data);
    Optional<presenca> findByAtletaAndData(atleta atleta, LocalDate data);
    Optional<presenca> findByAtletaAndEvento(atleta atleta, eventos evento);
    List<presenca> findByEvento(eventos evento);
    List<presenca> findByAtletaId(UUID atletaId);

}
