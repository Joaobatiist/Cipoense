// src/main/java/com/br/Entity/ComunicadoStatusPorUsuario.java
package com.br.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comunicado_status_por_usuario")
public class comunicadoStatus {

    @Id
    @UuidGenerator
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comunicado_id", nullable = true)
    private comunicado comunicado;

    // Relacionamento 1/2: Para Atletas
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atleta_id",nullable = true)
    private atleta atleta;

    // Relacionamento 2/2: PARA TODOS OS FUNCIONÁRIOS (Unificado)
    // Este campo substitui coordenador, supervisor e tecnico
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id") // Defina explicitamente o nome da coluna para clareza
    private funcionario funcionario;

    // Os campos removidos eram:
    // private coordenador coordenador;
    // private supervisor supervisor;
    // private tecnico tecnico;

    @Column(nullable = false)
    private boolean ocultado = false; // Indica se o usuário "excluiu" da sua sessão

    // -------------------------------------------------------------------------
    // Métodos Auxiliares Atualizados
    // -------------------------------------------------------------------------

    // Indica se é um atleta
    public boolean isForAtleta() { return this.atleta != null; }

    public boolean isForFuncionario() { return this.funcionario != null; }

    public UUID getAssociatedUserId() {
        if (atleta != null) return atleta.getId();
        if (funcionario != null) return funcionario.getId();
        return null;
    }

    public String getAssociatedUserRole() {
        if (atleta != null) return "ATLETA";
        // Retorna a Role real (COORDENADOR, SUPERVISOR, TECNICO) que está no objeto Funcionario
        if (funcionario != null) return funcionario.getRole().name();
        return null;
    }
}