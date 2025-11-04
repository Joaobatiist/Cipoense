package com.br.Request;

import com.br.Enums.subDivisao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class eventoCreateRequest {
    private LocalDate data;
    private String descricao;
    private String professor;
    private String local;
    private String horario;
    private subDivisao subDivisao;
}