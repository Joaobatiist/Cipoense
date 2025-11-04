package com.br.Response;

import com.br.Enums.subDivisao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class eventoResponse {
    private UUID id;
    private LocalDate data;
    private String descricao;
    private String professor;
    private String local;
    private String horario;
    private subDivisao subDivisao;

    // Método de conversão
    public static eventoResponse fromEntity(com.br.Entity.eventos evento) {
        return new eventoResponse(
                evento.getId(),
                evento.getData(),
                evento.getDescricao(),
                evento.getProfessor(),
                evento.getLocal(),
                evento.getHorario(),
                evento.getSubDivisao()
        );
    }
}