package com.br.Request;

import com.br.Enums.subDivisao;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class eventoRequest {
    private String data;
    private String descricao;
    private String professor;
    private String local;
    private String horario;
    private subDivisao subDivisao;
    private List<presencaRequest> presencas; // lista de atletas e presença

}