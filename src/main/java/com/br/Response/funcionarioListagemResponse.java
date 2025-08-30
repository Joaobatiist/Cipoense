package com.br.Response;


import com.br.Enums.Role;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class funcionarioListagemResponse {

    private Long id;
    private String nome;
    private String email;
    private LocalDate dataNascimento;
    private String cpf;
    private String telefone;
    private Role roles;
    private String uniqueId;

    public funcionarioListagemResponse(String uniqueId, Long id, String nome, String cpf, LocalDate dataNascimento, String email, Role roles, String telefone) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.email = email;
        this.roles = roles;
        this.telefone = telefone;
        this.uniqueId = roles + "_" + id;

    }
}
