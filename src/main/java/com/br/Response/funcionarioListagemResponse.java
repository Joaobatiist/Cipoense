package com.br.Response;


import com.br.Enums.role;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class funcionarioListagemResponse {

    private UUID id;
    private String nome;
    private String email;
    private LocalDate dataNascimento;
    private String cpf;
    private String telefone;
    private role roles;
    private String uniqueId;

    public funcionarioListagemResponse(String uniqueId, UUID id, String nome, String cpf, LocalDate dataNascimento, String email, role roles, String telefone) {
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
