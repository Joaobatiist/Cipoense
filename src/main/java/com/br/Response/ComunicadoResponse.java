package com.br.Response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComunicadoResponse {
    private Long id;
    private String assunto;
    private String mensagem;
    private LocalDate dataEnvio; // Renomeado para 'dataEnvio' para corresponder ao frontend

    // Representa um destinatário genérico para o frontend
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DestinatarioDTO {
        private Long id;
        private String nome;
        private String tipo; // "Aluno", "Coordenador", "Supervisor", "Tecnico"
    }

    private List<DestinatarioDTO> destinatarios;
    private  DestinatarioDTO remetente;// Lista consolidada de destinatários
}