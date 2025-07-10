package com.br.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="reponsavel")
public class  Responsavel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;
    private String nome;
    @Column(unique = true)
    private String telefone;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String cpf;

}