package com.br.Request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class presencaRequest {
    private Long atletaId;
    private Boolean presente;
    private LocalDate data;
}
