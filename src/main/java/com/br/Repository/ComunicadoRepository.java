package com.br.Repository;

import com.br.Entity.Comunicado;
import com.br.Entity.Coordenador;
import com.br.Entity.Supervisor;
import com.br.Entity.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComunicadoRepository extends JpaRepository<Comunicado, Long> {

    // Busca todos os comunicados com todos os destinatários e remetentes carregados
    @Query("SELECT DISTINCT c FROM Comunicado c " + // DISTINCT para evitar duplicação em ManyToMany
            "LEFT JOIN FETCH c.destinatariosAlunos da " +
            "LEFT JOIN FETCH c.destinatariosCoordenadores dc " +
            "LEFT JOIN FETCH c.destinatariosSupervisores ds " +
            "LEFT JOIN FETCH c.destinatariosTecnicos dt " +
            "LEFT JOIN FETCH c.remetenteCoordenador rc " +
            "LEFT JOIN FETCH c.remetenteSupervisor rs " +
            "LEFT JOIN FETCH c.remetenteTecnico rt")
    List<Comunicado> findAllWithAllDetails();

    // Busca comunicado por ID com todos os destinatários e remetentes carregados
    @Query("SELECT c FROM Comunicado c " +
            "LEFT JOIN FETCH c.destinatariosAlunos da " +
            "LEFT JOIN FETCH c.destinatariosCoordenadores dc " +
            "LEFT JOIN FETCH c.destinatariosSupervisores ds " +
            "LEFT JOIN FETCH c.destinatariosTecnicos dt " +
            "LEFT JOIN FETCH c.remetenteCoordenador rc " +
            "LEFT JOIN FETCH c.remetenteSupervisor rs " +
            "LEFT JOIN FETCH c.remetenteTecnico rt " +
            "WHERE c.id = :id")
    Optional<Comunicado> findByIdWithAllDetails(@Param("id") Long id);


    // Métodos findByRemetenteX com FETCH JOIN para carregar detalhes
    @Query("SELECT c FROM Comunicado c " +
            "LEFT JOIN FETCH c.destinatariosAlunos da " +
            "LEFT JOIN FETCH c.destinatariosCoordenadores dc " +
            "LEFT JOIN FETCH c.destinatariosSupervisores ds " +
            "LEFT JOIN FETCH c.destinatariosTecnicos dt " +
            "LEFT JOIN FETCH c.remetenteCoordenador rc " +
            "LEFT JOIN FETCH c.remetenteSupervisor rs " +
            "LEFT JOIN FETCH c.remetenteTecnico rt " +
            "WHERE c.remetenteCoordenador = :coordenador")
    List<Comunicado> findByRemetenteCoordenadorWithDetails(@Param("coordenador") Coordenador coordenador);

    @Query("SELECT c FROM Comunicado c " +
            "LEFT JOIN FETCH c.destinatariosAlunos da " +
            "LEFT JOIN FETCH c.destinatariosCoordenadores dc " +
            "LEFT JOIN FETCH c.destinatariosSupervisores ds " +
            "LEFT JOIN FETCH c.destinatariosTecnicos dt " +
            "LEFT JOIN FETCH c.remetenteCoordenador rc " +
            "LEFT JOIN FETCH c.remetenteSupervisor rs " +
            "LEFT JOIN FETCH c.remetenteTecnico rt " +
            "WHERE c.remetenteSupervisor = :supervisor")
    List<Comunicado> findByRemetenteSupervisorWithDetails(@Param("supervisor") Supervisor supervisor);

    @Query("SELECT c FROM Comunicado c " +
            "LEFT JOIN FETCH c.destinatariosAlunos da " +
            "LEFT JOIN FETCH c.destinatariosCoordenadores dc " +
            "LEFT JOIN FETCH c.destinatariosSupervisores ds " +
            "LEFT JOIN FETCH c.destinatariosTecnicos dt " +
            "LEFT JOIN FETCH c.remetenteCoordenador rc " +
            "LEFT JOIN FETCH c.remetenteSupervisor rs " +
            "LEFT JOIN FETCH c.remetenteTecnico rt " +
            "WHERE c.remetenteTecnico = :tecnico")
    List<Comunicado> findByRemetenteTecnicoWithDetails(@Param("tecnico") Tecnico tecnico);


    // NOVOS MÉTODOS: Buscar comunicados onde o usuário é destinatário e NÃO FOI OCULTADO
    // Adicionado `LEFT JOIN` com `ComunicadoStatusPorUsuario` e verificado `cs.ocultado = false` ou `cs.id IS NULL`
    // Isso garante que comunicados sem um registro de status (ou seja, nunca ocultados) também sejam retornados
    @Query("SELECT DISTINCT c FROM Comunicado c " +
            "LEFT JOIN FETCH c.destinatariosAlunos da " +
            "LEFT JOIN FETCH c.destinatariosCoordenadores dc " +
            "LEFT JOIN FETCH c.destinatariosSupervisores ds " +
            "LEFT JOIN FETCH c.destinatariosTecnicos dt " +
            "LEFT JOIN FETCH c.remetenteCoordenador rc " +
            "LEFT JOIN FETCH c.remetenteSupervisor rs " +
            "LEFT JOIN FETCH c.remetenteTecnico rt " +
            "LEFT JOIN ComunicadoStatus cs ON cs.comunicado = c AND cs.aluno.id = :alunoId " +
            "WHERE da.id = :alunoId AND (cs.id IS NULL OR cs.ocultado = false)")
    List<Comunicado> findComunicadosByDestinatarioAlunoIdAndNotOcultado(@Param("alunoId") Long alunoId);

    @Query("SELECT DISTINCT c FROM Comunicado c " +
            "LEFT JOIN FETCH c.destinatariosAlunos da " +
            "LEFT JOIN FETCH c.destinatariosCoordenadores dc " +
            "LEFT JOIN FETCH c.destinatariosSupervisores ds " +
            "LEFT JOIN FETCH c.destinatariosTecnicos dt " +
            "LEFT JOIN FETCH c.remetenteCoordenador rc " +
            "LEFT JOIN FETCH c.remetenteSupervisor rs " +
            "LEFT JOIN FETCH c.remetenteTecnico rt " +
            "LEFT JOIN ComunicadoStatus cs ON cs.comunicado = c AND cs.coordenador.id = :coordenadorId " +
            "WHERE dc.id = :coordenadorId AND (cs.id IS NULL OR cs.ocultado = false)")
    List<Comunicado> findComunicadosByDestinatarioCoordenadorIdAndNotOcultado(@Param("coordenadorId") Long coordenadorId);

    @Query("SELECT DISTINCT c FROM Comunicado c " +
            "LEFT JOIN FETCH c.destinatariosAlunos da " +
            "LEFT JOIN FETCH c.destinatariosCoordenadores dc " +
            "LEFT JOIN FETCH c.destinatariosSupervisores ds " +
            "LEFT JOIN FETCH c.destinatariosTecnicos dt " +
            "LEFT JOIN FETCH c.remetenteCoordenador rc " +
            "LEFT JOIN FETCH c.remetenteSupervisor rs " +
            "LEFT JOIN FETCH c.remetenteTecnico rt " +
            "LEFT JOIN ComunicadoStatus cs ON cs.comunicado = c AND cs.supervisor.id = :supervisorId " +
            "WHERE ds.id = :supervisorId AND (cs.id IS NULL OR cs.ocultado = false)")
    List<Comunicado> findComunicadosByDestinatarioSupervisorIdAndNotOcultado(@Param("supervisorId") Long supervisorId);

    @Query("SELECT DISTINCT c FROM Comunicado c " +
            "LEFT JOIN FETCH c.destinatariosAlunos da " +
            "LEFT JOIN FETCH c.destinatariosCoordenadores dc " +
            "LEFT JOIN FETCH c.destinatariosSupervisores ds " +
            "LEFT JOIN FETCH c.destinatariosTecnicos dt " +
            "LEFT JOIN FETCH c.remetenteCoordenador rc " +
            "LEFT JOIN FETCH c.remetenteSupervisor rs " +
            "LEFT JOIN FETCH c.remetenteTecnico rt " +
            "LEFT JOIN ComunicadoStatus cs ON cs.comunicado = c AND cs.tecnico.id = :tecnicoId " +
            "WHERE dt.id = :tecnicoId AND (cs.id IS NULL OR cs.ocultado = false)")
    List<Comunicado> findComunicadosByDestinatarioTecnicoIdAndNotOcultado(@Param("tecnicoId") Long tecnicoId);
}