package com.br.Repository;

import com.br.Entity.comunicado;
import com.br.Entity.funcionario; // Substituição: Coordenador, Supervisor, Tecnico => funcionario
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface comunicadoRepository extends JpaRepository<comunicado, UUID> {

    // 1. Busca todos os comunicados com todos os destinatários e remetentes carregados
    @Query("SELECT DISTINCT c FROM comunicado c " + // DISTINCT para evitar duplicação em ManyToMany
            "LEFT JOIN FETCH c.destinatariosAtletas da " +
            "LEFT JOIN FETCH c.destinatariosFuncionarios df " + // Unificado
            "LEFT JOIN FETCH c.remetenteFuncionario rf") // Unificado
    List<comunicado> findAllWithAllDetails();

    // 2. Busca comunicado por ID com todos os destinatários e remetentes carregados
    @Query("SELECT c FROM comunicado c " +
            "LEFT JOIN FETCH c.destinatariosAtletas da " +
            "LEFT JOIN FETCH c.destinatariosFuncionarios df " + // Unificado
            "LEFT JOIN FETCH c.remetenteFuncionario rf " + // Unificado
            "WHERE c.id = :id")
    Optional<comunicado> findByIdWithAllDetails(@Param("id") UUID id);


    // 3. Método findByRemetenteFuncionario com FETCH JOIN para carregar detalhes
    // Este método substitui findByRemetenteCoordenadorWithDetails, findByRemetenteSupervisorWithDetails e findByRemetenteTecnicoWithDetails
    @Query("SELECT c FROM comunicado c " +
            "LEFT JOIN FETCH c.destinatariosAtletas da " +
            "LEFT JOIN FETCH c.destinatariosFuncionarios df " + // Unificado
            "LEFT JOIN FETCH c.remetenteFuncionario rf " + // Unificado
            "WHERE c.remetenteFuncionario = :funcionario")
    List<comunicado> findByRemetenteFuncionarioWithDetails(@Param("funcionario") funcionario funcionario);


    // 4. NOVOS MÉTODOS: Buscar comunicados onde o usuário é destinatário e NÃO FOI OCULTADO
    @Query("SELECT DISTINCT c FROM comunicado c " +
            "LEFT JOIN FETCH c.destinatariosAtletas da " +
            "LEFT JOIN FETCH c.destinatariosFuncionarios df " + // Unificado
            "LEFT JOIN FETCH c.remetenteFuncionario rf " + // Unificado
            "LEFT JOIN comunicadoStatus cs ON cs.comunicado = c AND cs.atleta.id = :atletaId " +
            "WHERE da.id = :atletaId AND (cs.id IS NULL OR cs.ocultado = false)")
    List<comunicado> findComunicadosByDestinatarioAtletaIdAndNotOcultado(@Param("atletaId") UUID atletaId);

    // 5. Método para buscar comunicados para o novo destinatário 'Funcionario'
    // Substitui findComunicadosByDestinatarioCoordenadorIdAndNotOcultado, ...SupervisorIdAndNotOcultado e ...TecnicoIdAndNotOcultado
    @Query("SELECT DISTINCT c FROM comunicado c " +
            "LEFT JOIN FETCH c.destinatariosAtletas da " +
            "LEFT JOIN FETCH c.destinatariosFuncionarios df " + // Unificado
            "LEFT JOIN FETCH c.remetenteFuncionario rf " + // Unificado
            "LEFT JOIN comunicadoStatus cs ON cs.comunicado = c AND cs.funcionario.id = :funcionarioId " + // Assumindo que comunicadoStatus também foi atualizado para referenciar 'funcionario'
            "WHERE df.id = :funcionarioId AND (cs.id IS NULL OR cs.ocultado = false)")
    List<comunicado> findComunicadosByDestinatarioFuncionarioIdAndNotOcultado(@Param("funcionarioId") UUID funcionarioId);
}