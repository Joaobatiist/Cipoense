package com.br.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.Valid; // Importação para @Valid
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class criarAvaliacaoRequest {

    // Alterado de List<Long> para Long, pois um relatório geral é para um único atleta
    @NotNull(message = "O ID do atleta é obrigatório.")
    private UUID atletaId;

    private String userName;

    @NotNull(message = "A data da avaliação é obrigatória.")
    private LocalDate dataAvaliacao;

    private String subDivisao;

    private String posicao;

    @NotNull(message = "O período de treino é obrigatório.")
    private String periodoTreino;

    @Size(max = 2000, message = "O feedback do treinador não pode exceder 2000 caracteres.")
    private String feedbackTreinador;

    @Size(max = 2000, message = "O feedback do avaliador não pode exceder 2000 caracteres.")
    private String feedbackAvaliador;

    @Size(max = 700, message = "Os pontos fortes não podem exceder 700 caracteres.")
    private String pontosFortes;

    @Size(max = 700, message = "Os pontos fracos não podem exceder 700 caracteres.")
    private String pontosFracos;

    @Size(max = 1000, message = "As áreas de aprimoramento não podem exceder 1000 caracteres.")
    private String areasAprimoramento;

    @Size(max = 1000, message = "As metas e objetivos não podem exceder 1000 caracteres.")
    private String metasObjetivos;

    // DTOs aninhados para os relatórios de desempenho e tático/psicológico
    @Valid // Garante que as validações dentro de RelatorioDesempenhoRequest sejam aplicadas
    @NotNull(message = "Os dados de desempenho são obrigatórios.")
    private relatorioDesempenhoRequest relatorioDesempenho;

    @Valid // Garante que as validações dentro de RelatorioTaticoPsicologicoRequest sejam aplicadas
    @NotNull(message = "Os dados táticos/psicológicos são obrigatórios.")
    private relatorioTaticoPsicologicoRequest relatorioTaticoPsicologico;
}
