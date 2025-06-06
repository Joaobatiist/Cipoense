package com.br.Repository;
import com.br.Entity.Comunicado;
import com.br.Entity.ComunicadoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComunicadoStatusRepository extends JpaRepository<ComunicadoStatus, Long> {

    // Para encontrar o status de um comunicado específico para um aluno
    Optional<ComunicadoStatus> findByComunicadoAndAlunoId(Comunicado comunicado, Long alunoId);
    // Para encontrar o status de um comunicado específico para um coordenador
    Optional<ComunicadoStatus> findByComunicadoAndCoordenadorId(Comunicado comunicado, Long coordenadorId);

    Optional<ComunicadoStatus> findByComunicadoAndSupervisorId(Comunicado comunicado, Long supervisorId);
    Optional<ComunicadoStatus> findByComunicadoAndTecnicoId(Comunicado comunicado, Long tecnicoId);

    // Método para buscar pelo comunicado e qualquer tipo de usuário (pode ser mais complexo)
    // Este é um exemplo simplificado, você pode precisar de mais métodos específicos
    Optional<ComunicadoStatus> findByComunicadoIdAndAlunoIdOrComunicadoIdAndCoordenadorIdOrComunicadoIdAndSupervisorIdOrComunicadoIdAndTecnicoId(
            Long comunicadoId, Long alunoId, Long comunicadoId2, Long coordenadorId, Long comunicadoId3, Long supervisorId, Long comunicadoId4, Long tecnicoId);


    // Para deletar todos os status associados a um comunicado quando ele é deletado permanentemente
    List<ComunicadoStatus> findByComunicado(Comunicado comunicado);
}