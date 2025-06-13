package com.br.Request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PresencaRequest {
    private Long atletaId;
    private Boolean presente;
    private LocalDate data;
}
