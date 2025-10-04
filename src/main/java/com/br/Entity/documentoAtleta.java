package com.br.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "documento_atleta")
public class documentoAtleta {
    @Id
    @UuidGenerator
    @GeneratedValue
    private UUID id;

    private String nome;
    private String url;
    private String tipo; // PDF, EXAME, etc.

    @ManyToOne
    @JoinColumn(name = "atleta_id")
    private atleta atleta;
}