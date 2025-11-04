package com.br.Request;

import lombok.Data; // Para @Getter, @Setter, @NoArgsConstructor

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data // Gera Getters, Setters, toString, equals, hashCode e construtor padrão
public class comunicadoRequest {
    private String assunto;
    private String mensagem;
    private LocalDate data; // Opcional, pode ser gerado no backend

    // Listas de IDs para cada tipo de destinatário
    private List<UUID> atletasIds;
    private List<UUID> funcionarioIds;
}