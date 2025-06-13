package com.br.Repository;

import com.br.Entity.Atleta;
import com.br.Entity.Presenca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface PresencaRepository extends JpaRepository <Presenca, Long >{
    Optional<Presenca> findByAtletaAndData(Atleta atleta, LocalDate data);
}
