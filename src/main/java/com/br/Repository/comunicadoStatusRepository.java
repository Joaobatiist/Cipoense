package com.br.Repository;
import com.br.Entity.comunicado;
import com.br.Entity.comunicadoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface comunicadoStatusRepository extends JpaRepository<comunicadoStatus, UUID> {

    // Para encontrar o status de um comunicado específico para um aluno
    Optional<comunicadoStatus> findByComunicadoAndAtletaId(comunicado comunicado, UUID atletaId);

    // Para encontrar o status de um comunicado específico para um funcionario
    Optional<comunicadoStatus> findByComunicadoAndFuncionarioId(comunicado comunicado, UUID funcionarioId);

    // Para deletar todos os status associados a um comunicado quando ele é deletado permanentemente
    List<comunicadoStatus> findByComunicado(comunicado comunicado);
}