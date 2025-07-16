package com.br.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioTaticoPsicologicoRequest {

    @NotNull(message = "A esportividade é obrigatória.")
    @Min(value = 1, message = "A esportividade deve ser no mínimo 1.")
    @Max(value = 5, message = "A esportividade deve ser no máximo 5.")
    private Integer esportividade;

    @NotNull(message = "A disciplina é obrigatória.")
    @Min(value = 1, message = "A disciplina deve ser no mínimo 1.")
    @Max(value = 5, message = "A disciplina deve ser no máximo 5.")
    private Integer disciplina;

    @NotNull(message = "O foco é obrigatório.")
    @Min(value = 1, message = "O foco deve ser no mínimo 1.")
    @Max(value = 5, message = "O foco deve ser no máximo 5.")
    private Integer foco;

    @NotNull(message = "A confiança é obrigatória.")
    @Min(value = 1, message = "A confiança deve ser no mínimo 1.")
    @Max(value = 5, message = "A confiança deve ser no máximo 5.")
    private Integer confianca;

    @NotNull(message = "A tomada de decisões é obrigatória.")
    @Min(value = 1, message = "A tomada de decisões deve ser no mínimo 1.")
    @Max(value = 5, message = "A tomada de decisões deve ser no máximo 5.")
    private Integer tomadaDecisoes;

    @NotNull(message = "O compromisso é obrigatório.")
    @Min(value = 1, message = "O compromisso deve ser no mínimo 1.")
    @Max(value = 5, message = "O compromisso deve ser no máximo 5.")
    private Integer compromisso;

    @NotNull(message = "A liderança é obrigatória.")
    @Min(value = 1, message = "A liderança deve ser no mínimo 1.")
    @Max(value = 5, message = "A liderança deve ser no máximo 5.")
    private Integer lideranca;

    @NotNull(message = "O trabalho em equipe é obrigatório.")
    @Min(value = 1, message = "O trabalho em equipe deve ser no mínimo 1.")
    @Max(value = 5, message = "O trabalho em equipe deve ser no máximo 5.")
    private Integer trabalhoEmEquipe;

    @NotNull(message = "Atributos físicos são obrigatórios.")
    @Min(value = 1, message = "Atributos físicos devem ser no mínimo 1.")
    @Max(value = 5, message = "Atributos físicos devem ser no máximo 5.")
    private Integer atributosFisicos;

    @NotNull(message = "Atuar sob pressão é obrigatório.")
    @Min(value = 1, message = "Atuar sob pressão deve ser no mínimo 1.")
    @Max(value = 5, message = "Atuar sob pressão deve ser no máximo 5.")
    private Integer atuarSobPressao;
}
