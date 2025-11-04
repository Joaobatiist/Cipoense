package com.br.Request;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class presencaRequest {

    private UUID atletaId;
    private UUID eventoId;
    private Boolean presente;

}
