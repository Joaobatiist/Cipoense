package com.br.Repository;

import com.br.Entity.eventos;
import com.br.Enums.subDivisao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface eventosRepository extends JpaRepository<eventos, UUID> {
    List<eventos> findBySubDivisao(subDivisao subDivisao);
    Optional<eventos> findByLocalAndData(String Local, String Data);

    @Query("SELECT DISTINCT e FROM eventos e " +
            "LEFT JOIN FETCH e.atletasEscalados ae " +
            "JOIN atleta a ON a.id = :atletaId " +
            "WHERE e.subDivisao = a.subDivisao OR ae.id = :atletaId")
    List<eventos> findEventosByAtletaVisibility(@Param("atletaId") UUID atletaId);

    @Query("SELECT DISTINCT e FROM eventos e " +
                "LEFT JOIN FETCH e.atletasEscalados ae " +
                "WHERE e.subDivisao = :subdivisao OR ae.id = :atletaId")
    List<eventos> findEventosBySubdivisaoAndAtletaEscalado(
                @Param("subdivisao") subDivisao subdivisao,
                @Param("atletaId") UUID atletaId
    );
}
