package com.br.Repository;
import com.br.Entity.comunicado;
import com.br.Entity.comunicadoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface comunicadoStatusRepository extends JpaRepository<comunicadoStatus, Long> {

    // Para encontrar o status de um comunicado específico para um aluno
    Optional<comunicadoStatus> findByComunicadoAndAtletaId(comunicado comunicado, Long atletaId);
    // Para encontrar o status de um comunicado específico para um coordenador
    Optional<comunicadoStatus> findByComunicadoAndCoordenadorId(comunicado comunicado, Long coordenadorId);

    Optional<comunicadoStatus> findByComunicadoAndSupervisorId(comunicado comunicado, Long supervisorId);
    Optional<comunicadoStatus> findByComunicadoAndTecnicoId(comunicado comunicado, Long tecnicoId);


    Optional<comunicadoStatus> findByComunicadoIdAndAtletaIdOrComunicadoIdAndCoordenadorIdOrComunicadoIdAndSupervisorIdOrComunicadoIdAndTecnicoId(
            Long comunicadoId, Long atletaId, Long comunicadoId2, Long coordenadorId, Long comunicadoId3, Long supervisorId, Long comunicadoId4, Long tecnicoId);


    // Para deletar todos os status associados a um comunicado quando ele é deletado permanentemente
    List<comunicadoStatus> findByComunicado(comunicado comunicado);
}