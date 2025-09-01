package com.br.Repository;

import com.br.Entity.atleta;
import com.br.Entity.presenca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface presencaRepository extends JpaRepository <presenca, Long >{
    Optional<presenca> findByAtletaAndData(atleta atleta, LocalDate data);
    List<presenca> findByData(LocalDate data);

    // Pode ser útil também para buscar presenças de um atleta específico (opcional)

}
