// src/main/java/com/br/Entity/ComunicadoStatusPorUsuario.java
package com.br.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comunicado_status_por_usuario")
public class ComunicadoStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comunicado_id", nullable = false)
    private Comunicado comunicado;

    // Relacionamentos para os tipos de usuário (apenas um deve ser preenchido)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordenador_id")
    private Coordenador coordenador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    private Supervisor supervisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnico_id")
    private Tecnico tecnico;

    @Column(nullable = false)
    private boolean ocultado = false; // Indica se o usuário "excluiu" da sua sessão

    // Métodos para verificar o tipo de usuário associado
    public boolean isForAluno() { return this.aluno != null; }
    public boolean isForCoordenador() { return this.coordenador != null; }
    public boolean isForSupervisor() { return this.supervisor != null; }
    public boolean isForTecnico() { return this.tecnico != null; }

    // Método auxiliar para obter o ID do usuário relacionado (para conveniência)
    public Long getAssociatedUserId() {
        if (aluno != null) return aluno.getId();
        if (coordenador != null) return coordenador.getId();
        if (supervisor != null) return supervisor.getId();
        if (tecnico != null) return tecnico.getId();
        return null;
    }
}