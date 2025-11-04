package com.br.Request;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class presencaRequest {

    private UUID atletaId;   // ID do atleta que está tendo a presença registrada
    private UUID eventoId;   // ID do evento onde a presença está sendo registrada
    private Boolean presente; // true se o atleta compareceu, false se não
    private LocalDate data;   // Data da presença (pode ser a data do evento)
}
