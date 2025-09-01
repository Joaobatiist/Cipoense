package com.br.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class historicoPresencaResponse {
    private Long id; // ID do registro de presença (da tabela 'presenca')
    private LocalDate data; // Data da presença
    private Boolean presente; // Status da presença
    private Long atletaId; // ID do atleta
    private String nomeAtleta; // Nome do atleta
}