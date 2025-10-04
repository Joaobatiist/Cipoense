package com.br.Repository;

import com.br.Entity.comunicado;
import com.br.Entity.coordenador;
import com.br.Entity.supervisor;
import com.br.Entity.tecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface comunicadoRepository extends JpaRepository<comunicado, UUID> {

    // Busca todos os comunicados com todos os destinatários e remetentes carregados
    @Query("SELECT DISTINCT c FROM comunicado c " + // DISTINCT para evitar duplicação em ManyToMany
            "LEFT JOIN FETCH c.destinatariosAtletas da " +
            "LEFT JOIN FETCH c.destinatariosCoordenadores dc " +
            "LEFT JOIN FETCH c.destinatariosSupervisores ds " +
            "LEFT JOIN FETCH c.destinatariosTecnicos dt " +
            "LEFT JOIN FETCH c.remetenteCoordenador rc " +
            "LEFT JOIN FETCH c.remetenteSupervisor rs " +
            "LEFT JOIN FETCH c.remetenteTecnico rt")
    List<comunicado> findAllWithAllDetails();

    // Busca comunicado por ID com todos os destinatários e remetentes carregados
    @Query("SELECT c FROM comunicado c " +
            "LEFT JOIN FETCH c.destinatariosAtletas da " +
            "LEFT JOIN FETCH c.destinatariosCoordenadores dc " +
            "LEFT JOIN FETCH c.destinatariosSupervisores ds " +
            "LEFT JOIN FETCH c.destinatariosTecnicos dt " +
            "LEFT JOIN FETCH c.remetenteCoordenador rc " +
            "LEFT JOIN FETCH c.remetenteSupervisor rs " +
            "LEFT JOIN FETCH c.remetenteTecnico rt " +
            "WHERE c.id = :id")
    Optional<comunicado> findByIdWithAllDetails(@Param("id") UUID id);


    // Métodos findByRemetenteX com FETCH JOIN para carregar detalhes
    @Query("SELECT c FROM comunicado c " +
            "LEFT JOIN FETCH c.destinatariosAtletas da " +
            "LEFT JOIN FETCH c.destinatariosCoordenadores dc " +
            "LEFT JOIN FETCH c.destinatariosSupervisores ds " +
            "LEFT JOIN FETCH c.destinatariosTecnicos dt " +
            "LEFT JOIN FETCH c.remetenteCoordenador rc " +
            "LEFT JOIN FETCH c.remetenteSupervisor rs " +
            "LEFT JOIN FETCH c.remetenteTecnico rt " +
            "WHERE c.remetenteCoordenador = :coordenador")
    List<comunicado> findByRemetenteCoordenadorWithDetails(@Param("coordenador") coordenador coordenador);

    @Query("SELECT c FROM comunicado c " +
            "LEFT JOIN FETCH c.destinatariosAtletas da " +
            "LEFT JOIN FETCH c.destinatariosCoordenadores dc " +
            "LEFT JOIN FETCH c.destinatariosSupervisores ds " +
            "LEFT JOIN FETCH c.destinatariosTecnicos dt " +
            "LEFT JOIN FETCH c.remetenteCoordenador rc " +
            "LEFT JOIN FETCH c.remetenteSupervisor rs " +
            "LEFT JOIN FETCH c.remetenteTecnico rt " +
            "WHERE c.remetenteSupervisor = :supervisor")
    List<comunicado> findByRemetenteSupervisorWithDetails(@Param("supervisor") supervisor supervisor);

    @Query("SELECT c FROM comunicado c " +
            "LEFT JOIN FETCH c.destinatariosAtletas da " +
            "LEFT JOIN FETCH c.destinatariosCoordenadores dc " +
            "LEFT JOIN FETCH c.destinatariosSupervisores ds " +
            "LEFT JOIN FETCH c.destinatariosTecnicos dt " +
            "LEFT JOIN FETCH c.remetenteCoordenador rc " +
            "LEFT JOIN FETCH c.remetenteSupervisor rs " +
            "LEFT JOIN FETCH c.remetenteTecnico rt " +
            "WHERE c.remetenteTecnico = :tecnico")
    List<comunicado> findByRemetenteTecnicoWithDetails(@Param("tecnico") tecnico tecnico);


    // NOVOS MÉTODOS: Buscar comunicados onde o usuário é destinatário e NÃO FOI OCULTADO
    // Adicionado `LEFT JOIN` com `ComunicadoStatusPorUsuario` e verificado `cs.ocultado = false` ou `cs.id IS NULL`
    // Isso garante que comunicados sem um registro de status (ou seja, nunca ocultados) também sejam retornados
    @Query("SELECT DISTINCT c FROM comunicado c " +
            "LEFT JOIN FETCH c.destinatariosAtletas da " +
            "LEFT JOIN FETCH c.destinatariosCoordenadores dc " +
            "LEFT JOIN FETCH c.destinatariosSupervisores ds " +
            "LEFT JOIN FETCH c.destinatariosTecnicos dt " +
            "LEFT JOIN FETCH c.remetenteCoordenador rc " +
            "LEFT JOIN FETCH c.remetenteSupervisor rs " +
            "LEFT JOIN FETCH c.remetenteTecnico rt " +
            "LEFT JOIN comunicadoStatus cs ON cs.comunicado = c AND cs.atleta.id = :atletaId " +
            "WHERE da.id = :atletaId AND (cs.id IS NULL OR cs.ocultado = false)")
    List<comunicado> findComunicadosByDestinatarioAtletaIdAndNotOcultado(@Param("atletaId") UUID atletaId);

    @Query("SELECT DISTINCT c FROM comunicado c " +
            "LEFT JOIN FETCH c.destinatariosAtletas da " +
            "LEFT JOIN FETCH c.destinatariosCoordenadores dc " +
            "LEFT JOIN FETCH c.destinatariosSupervisores ds " +
            "LEFT JOIN FETCH c.destinatariosTecnicos dt " +
            "LEFT JOIN FETCH c.remetenteCoordenador rc " +
            "LEFT JOIN FETCH c.remetenteSupervisor rs " +
            "LEFT JOIN FETCH c.remetenteTecnico rt " +
            "LEFT JOIN comunicadoStatus cs ON cs.comunicado = c AND cs.coordenador.id = :coordenadorId " +
            "WHERE dc.id = :coordenadorId AND (cs.id IS NULL OR cs.ocultado = false)")
    List<comunicado> findComunicadosByDestinatarioCoordenadorIdAndNotOcultado(@Param("coordenadorId") UUID coordenadorId);

    @Query("SELECT DISTINCT c FROM comunicado c " +
            "LEFT JOIN FETCH c.destinatariosAtletas da " +
            "LEFT JOIN FETCH c.destinatariosCoordenadores dc " +
            "LEFT JOIN FETCH c.destinatariosSupervisores ds " +
            "LEFT JOIN FETCH c.destinatariosTecnicos dt " +
            "LEFT JOIN FETCH c.remetenteCoordenador rc " +
            "LEFT JOIN FETCH c.remetenteSupervisor rs " +
            "LEFT JOIN FETCH c.remetenteTecnico rt " +
            "LEFT JOIN comunicadoStatus cs ON cs.comunicado = c AND cs.supervisor.id = :supervisorId " +
            "WHERE ds.id = :supervisorId AND (cs.id IS NULL OR cs.ocultado = false)")
    List<comunicado> findComunicadosByDestinatarioSupervisorIdAndNotOcultado(@Param("supervisorId") UUID supervisorId);

    @Query("SELECT DISTINCT c FROM comunicado c " +
            "LEFT JOIN FETCH c.destinatariosAtletas da " +
            "LEFT JOIN FETCH c.destinatariosCoordenadores dc " +
            "LEFT JOIN FETCH c.destinatariosSupervisores ds " +
            "LEFT JOIN FETCH c.destinatariosTecnicos dt " +
            "LEFT JOIN FETCH c.remetenteCoordenador rc " +
            "LEFT JOIN FETCH c.remetenteSupervisor rs " +
            "LEFT JOIN FETCH c.remetenteTecnico rt " +
            "LEFT JOIN comunicadoStatus cs ON cs.comunicado = c AND cs.tecnico.id = :tecnicoId " +
            "WHERE dt.id = :tecnicoId AND (cs.id IS NULL OR cs.ocultado = false)")
    List<comunicado> findComunicadosByDestinatarioTecnicoIdAndNotOcultado(@Param("tecnicoId") UUID tecnicoId);
}