package com.br.Response;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RelatorioTaticoPsicologicoResponse {
    private Long id;
    private int esportividade;
    private int disciplina;
    private int foco;
    private int confianca;
    private int tomadaDecisoes;
    private int compromisso;
    private int lideranca;
    private int trabalhoEmEquipe;
    private int atributosFisicos;
    private int atuarSobPressao;
    public RelatorioTaticoPsicologicoResponse(com.br.Entity.RelatorioTaticoPsicologico entity) {
        this.id = entity.getId();
        this.esportividade = entity.getEsportividade();
        this.disciplina = entity.getDisciplina();
        this.foco = entity.getFoco();
        this.confianca = entity.getConfianca();
        this.tomadaDecisoes = entity.getTomadaDecisoes();
        this.compromisso = entity.getCompromisso();
        this.lideranca = entity.getLideranca();
        this.trabalhoEmEquipe = entity.getTrabalhoEmEquipe();
        this.atributosFisicos = entity.getAtributosFisicos();
        this.atuarSobPressao = entity.getAtuarSobPressao();
    }
}
