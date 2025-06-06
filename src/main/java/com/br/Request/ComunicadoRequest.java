package com.br.Request;

import lombok.Data; // Para @Getter, @Setter, @NoArgsConstructor

import java.time.LocalDate;
import java.util.List;

@Data // Gera Getters, Setters, toString, equals, hashCode e construtor padrão
public class ComunicadoRequest {
    private String assunto;
    private String mensagem;
    private LocalDate data; // Opcional, pode ser gerado no backend

    // Listas de IDs para cada tipo de destinatário
    private List<Long> alunoIds;
    private List<Long> coordenadorIds;
    private List<Long> supervisorIds;
    private List<Long> tecnicoIds;
}