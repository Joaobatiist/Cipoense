// src/main/java/com/br/Response/AvaliacaoGeralResponse.java
package com.br.Response;

import com.br.Entity.RelatorioAvaliacaoGeral;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor // Este construtor @AllArgsConstructor é gerado pelo Lombok com TODOS os campos.
// O construtor personalizado que aceita RelatorioAvaliacaoGeral é manual.
public class AvaliacaoGeralResponse {

    private Long id;
    private Long atletaId;
    private String nomeAtleta;
    private String userName;
    private String dataAvaliacao; // Formatada como String para o frontend
    private String periodoTreino;
    private String subDivisao;
    private String feedbackTreinador;
    private String feedbackAvaliador;
    private String pontosFortes;
    private String pontosFracos;
    private String areasAprimoramento;
    private String metasObjetivos;

    // --- Inclua os DTOs para os sub-relatórios ---
    private RelatorioDesempenhoResponse relatorioDesempenho;
    private RelatorioTaticoPsicologicoResponse relatorioTaticoPsicologico;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // *** ESTE É O CONSTRUTOR QUE FALTAVA OU ESTAVA INCOMPLETO ***
    // Construtor que mapeia de Entidade RelatorioAvaliacaoGeral para este Response DTO
    public AvaliacaoGeralResponse(RelatorioAvaliacaoGeral relatorio) {
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

        // Mapeamento de SubDivisao:
        // Use esta linha se o campo 'subDivisao' na entidade RelatorioAvaliacaoGeral é uma String:
        this.subDivisao = String.valueOf(relatorio.getSubDivisao());
        // OU, use esta linha se o campo 'subDivisao' na entidade RelatorioAvaliacaoGeral é um ENUM SubDivisao:
        // this.subDivisao = relatorio.getSubDivisao() != null ? ((SubDivisao) relatorio.getSubDivisao()).name() : null;


        this.feedbackTreinador = relatorio.getFeedbackTreinador();
        this.feedbackAvaliador = relatorio.getFeedbackAvaliador();
        this.pontosFortes = relatorio.getPontosFortes();
        this.pontosFracos = relatorio.getPontosFracos();
        this.areasAprimoramento = relatorio.getAreasAprimoramento();
        this.metasObjetivos = relatorio.getMetasObjetivos();

        // --- Mapeamento dos Sub-Relatórios para seus respectivos DTOs ---
        // Cria um novo DTO de sub-relatório APENAS se a entidade do sub-relatório NÃO for nula
        if (relatorio.getRelatorioDesempenho() != null) {
            this.relatorioDesempenho = new RelatorioDesempenhoResponse(relatorio.getRelatorioDesempenho());
        } else {
            this.relatorioDesempenho = null; // Explicitamente define como null
        }

        if (relatorio.getRelatorioTaticoPsicologico() != null) {
            this.relatorioTaticoPsicologico = new RelatorioTaticoPsicologicoResponse(relatorio.getRelatorioTaticoPsicologico());
        } else {
            this.relatorioTaticoPsicologico = null; // Explicitamente define como null
        }
    }
}