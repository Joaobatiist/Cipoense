package com.br.Entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Responsavel {
    private String nome;
    private String telefone;
    private String email;
    private String cpf;
}
