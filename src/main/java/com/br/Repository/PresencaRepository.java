package com.br.Repository;

import com.br.Entity.Atleta;
import com.br.Entity.Presenca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PresencaRepository extends JpaRepository <Presenca, Long >{
    Optional<Presenca> findByAtletaAndData(Atleta atleta, LocalDate data);
    List<Presenca> findByData(LocalDate data);

    // Pode ser útil também para buscar presenças de um atleta específico (opcional)

}
