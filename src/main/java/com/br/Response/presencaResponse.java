package com.br.Response;

import com.br.Entity.atleta;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class presencaResponse {

    private UUID id;
    private UUID atletaId;
    private String nome;
    private Boolean presenca;
    private UUID eventoId;
    private String eventoDescricao;
}
