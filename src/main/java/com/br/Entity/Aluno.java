package com.br.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "aluno")
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="matricula", nullable = true)
    private Integer matricula;

    @Column(name="nome", nullable = false)
    private String nome;

    @Column(name="senha", nullable = false)
    private String senha;

    @Column(name="email", nullable = false)
    private String email;

    @Column(name = "dataDeNascimento")
    private LocalDate dataNascimento;

    @Column(name = "cpfResponsavel", nullable = false)
    private String cpfResponsavel;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "responsavel_id")
    private Responsavel responsavel;

    // Getters e Setters (gerados pelo Lombok)
}
