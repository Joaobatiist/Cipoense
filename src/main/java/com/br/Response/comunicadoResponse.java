package com.br.Response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class comunicadoResponse {
    private UUID id;
    private String assunto;
    private String mensagem;
    private String dataEnvio; // Renomeado para 'dataEnvio' para corresponder ao frontend

    // Representa um destinatário genérico para o frontend
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DestinatarioDTO {
        private UUID id;
        private String nome;
        private String tipo;
    }

    private List<DestinatarioDTO> destinatarios;
    private  DestinatarioDTO remetente;// Lista consolidada de destinatários
}