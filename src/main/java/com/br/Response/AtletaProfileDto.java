package com.br.Response;

import com.br.Enums.SubDivisao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AtletaProfileDto {
    private Long id;
    private Integer matricula;
    private String nome;
    private String email;
    private SubDivisao subDivisao;
    private String dataNascimento;
    private String foto;
    private String contatoResponsavel; // Este campo no DTO representará o telefone do Responsavel
    private List<DocumentoDto> documentos;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentoDto {
        private Long id;
        private String nome;
        private String url;
        private String tipo;
    }
}