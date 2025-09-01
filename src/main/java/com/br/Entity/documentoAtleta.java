package com.br.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "documento_atleta")
public class documentoAtleta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String url;
    private String tipo; // PDF, EXAME, etc.

    @ManyToOne
    @JoinColumn(name = "atleta_id")
    private atleta atleta;
}