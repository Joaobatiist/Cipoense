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

    Optional<presenca> findByAtletaAndData(atleta atleta, LocalDate data);
    List<presenca> findByData(LocalDate data);
    List<presenca> findByAtletaId(UUID atleta);
    Optional<presenca> findByAtletaAndEventoAndData(atleta atleta, eventos evento, LocalDate data);

}
