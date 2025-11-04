package com.br.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "presenca")
public class presenca {

    @Id
    @UuidGenerator
    @GeneratedValue
    private UUID id;

    @JsonBackReference("atleta-presenca")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atleta_id", nullable = false)
    private atleta atleta;

    // CORREÇÃO: Remover @JsonIgnore e manter apenas @JsonBackReference
    @JsonBackReference("evento-presenca")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private eventos evento;

    @Column(nullable = false)
    private Boolean presente;

    private LocalDate data;

    // ADICIONAR: Métodos auxiliares para evitar problemas de serialização
    public UUID getEventoId() {
        return evento != null ? evento.getId() : null;
    }

    public String getEventoDescricao() {
        return evento != null ? evento.getDescricao() : null;
    }
}