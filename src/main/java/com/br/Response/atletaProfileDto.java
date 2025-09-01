package com.br.Response;

import com.br.Enums.posicao;
import com.br.Enums.subDivisao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class atletaProfileDto {
    private Long id;
    private Integer matricula;
    private String nome;
    private String email;
    private subDivisao subDivisao;
    private posicao posicao;
    private String dataNascimento;
    private String foto;
    private String contatoResponsavel;
    private Boolean isAptoParaJogar;

    // --- NOVOS CAMPOS PARA PDF ENVIADO EM BASE64 ---
    private String documentoPdfBase64; // PDF em formato Base64 para o frontend
    private String documentoPdfContentType; // Tipo do conteúdo (ex: "application/pdf")
    // --- FIM DOS NOVOS CAMPOS ---

    private List<DocumentoDto> documentos;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentoDto {
        private Long id;
        private String nome;
        private String url; // Esta URL ainda pode ser usada para outros documentos que não sejam o PDF principal
        private String tipo;
    }
}