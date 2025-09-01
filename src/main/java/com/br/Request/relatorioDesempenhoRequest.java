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
public class relatorioDesempenhoRequest {

    @NotNull(message = "O controle é obrigatório.")
    @Min(value = 1, message = "O controle deve ser no mínimo 1.")
    @Max(value = 5, message = "O controle deve ser no máximo 5.")
    private Integer controle;

    @NotNull(message = "A recepção é obrigatória.")
    @Min(value = 1, message = "A recepção deve ser no mínimo 1.")
    @Max(value = 5, message = "A recepção deve ser no máximo 5.")
    private Integer recepcao;

    @NotNull(message = "Dribles são obrigatórios.")
    @Min(value = 1, message = "Dribles devem ser no mínimo 1.")
    @Max(value = 5, message = "Dribles devem ser no máximo 5.")
    private Integer dribles;

    @NotNull(message = "O passe é obrigatório.")
    @Min(value = 1, message = "O passe deve ser no mínimo 1.")
    @Max(value = 5, message = "O passe deve ser no máximo 5.")
    private Integer passe;

    @NotNull(message = "O tiro é obrigatório.")
    @Min(value = 1, message = "O tiro deve ser no mínimo 1.")
    @Max(value = 5, message = "O tiro deve ser no máximo 5.")
    private Integer tiro;

    @NotNull(message = "O cruzamento é obrigatório.")
    @Min(value = 1, message = "O cruzamento deve ser no mínimo 1.")
    @Max(value = 5, message = "O cruzamento deve ser no máximo 5.")
    private Integer cruzamento;

    @NotNull(message = "O giro é obrigatório.")
    @Min(value = 1, message = "O giro deve ser no mínimo 1.")
    @Max(value = 5, message = "O giro deve ser no máximo 5.")
    private Integer giro;

    @NotNull(message = "O manuseio de bola é obrigatório.")
    @Min(value = 1, message = "O manuseio de bola deve ser no mínimo 1.")
    @Max(value = 5, message = "O manuseio de bola deve ser no máximo 5.")
    private Integer manuseioDeBola;

    @NotNull(message = "A força do chute é obrigatória.")
    @Min(value = 1, message = "A força do chute deve ser no mínimo 1.")
    @Max(value = 5, message = "A força do chute deve ser no máximo 5.")
    private Integer forcaChute;

    @NotNull(message = "O gerenciamento de gols é obrigatório.")
    @Min(value = 1, message = "O gerenciamento de gols deve ser no mínimo 1.")
    @Max(value = 5, message = "O gerenciamento de gols deve ser no máximo 5.")
    private Integer gerenciamentoDeGols;

    @NotNull(message = "O jogo ofensivo é obrigatório.")
    @Min(value = 1, message = "O jogo ofensivo deve ser no mínimo 1.")
    @Max(value = 5, message = "O jogo ofensivo deve ser no máximo 5.")
    private Integer jogoOfensivo;

    @NotNull(message = "O jogo defensivo é obrigatório.")
    @Min(value = 1, message = "O jogo defensivo deve ser no mínimo 1.")
    @Max(value = 5, message = "O jogo defensivo deve ser no máximo 5.")
    private Integer jogoDefensivo;
}
