// src/main/java/com/br/Response/AvaliacaoGeralResponse.java
package com.br.Response;

import com.br.Entity.relatorioAvaliacaoGeral;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor // Este construtor @AllArgsConstructor é gerado pelo Lombok com TODOS os campos.
// O construtor personalizado que aceita RelatorioAvaliacaoGeral é manual.
public class avaliacaoGeralResponse {

    private UUID id;
    private UUID atletaId;
    private String nomeAtleta;
    private String userName;
    private String dataAvaliacao; // Formatada como String para o frontend
    private String periodoTreino;
    private String subDivisao;
    private String posicao;
    private String feedbackTreinador;
    private String feedbackAvaliador;
    private String pontosFortes;
    private String pontosFracos;
    private String areasAprimoramento;
    private String metasObjetivos;

    // --- Inclua os DTOs para os sub-relatórios ---
    private relatorioDesempenhoResponse relatorioDesempenho;
    private relatorioTaticoPsicologicoResponse relatorioTaticoPsicologico;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // *** ESTE É O CONSTRUTOR QUE FALTAVA OU ESTAVA INCOMPLETO ***
    // Construtor que mapeia de Entidade RelatorioAvaliacaoGeral para este Response DTO
    public avaliacaoGeralResponse(relatorioAvaliacaoGeral relatorio) {
        this.id = relatorio.getId();
        this.userName = relatorio.getUserName();
        this.dataAvaliacao = relatorio.getDataAvaliacao() != null ? relatorio.getDataAvaliacao().format(DATE_FORMATTER) : null;
        this.periodoTreino = relatorio.getPeriodoTreino();

        if (relatorio.getAtleta() != null) {
            this.atletaId = relatorio.getAtleta().getId();
            this.nomeAtleta = relatorio.getAtleta().getNome();
        } else {
            this.atletaId = null;
            this.nomeAtleta = "Atleta Desconhecido";
        }

        this.subDivisao = String.valueOf(relatorio.getSubDivisao());
        this.posicao = String.valueOf(relatorio.getPosicao());
        this.feedbackTreinador = relatorio.getFeedbackTreinador();
        this.feedbackAvaliador = relatorio.getFeedbackAvaliador();
        this.pontosFortes = relatorio.getPontosFortes();
        this.pontosFracos = relatorio.getPontosFracos();
        this.areasAprimoramento = relatorio.getAreasAprimoramento();
        this.metasObjetivos = relatorio.getMetasObjetivos();

        if (relatorio.getRelatorioDesempenho() != null) {
            this.relatorioDesempenho = new relatorioDesempenhoResponse(relatorio.getRelatorioDesempenho());
        } else {
            this.relatorioDesempenho = null; // Explicitamente define como null
        }

        if (relatorio.getRelatorioTaticoPsicologico() != null) {
            this.relatorioTaticoPsicologico = new relatorioTaticoPsicologicoResponse(relatorio.getRelatorioTaticoPsicologico());
        } else {
            this.relatorioTaticoPsicologico = null; // Explicitamente define como null
        }
    }
}