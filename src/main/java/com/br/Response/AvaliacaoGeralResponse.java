package com.br.Response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AvaliacaoGeralResponse {
    private Long relatorioId;
    private String userName;
    private String nomeAtleta;
    private String subDivisao;
    private LocalDate dataAvaliacao;
    private String periodoTreino;
}
