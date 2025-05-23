package com.br.Entity;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Aluno {
    private int matricula;
    private String nome;
    private String senha;
    private String email;
    private Data dataNascimento;
}
