package com.br.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class presencaResponse {
    private Long id;
    private String nome;
    private Boolean presenca;
}