package com.br.Response;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RelatorioDesempenhoResponse {
    private Long id;
    private int controle;
    private int recepcao;
    private int dribles;
    private int passe;
    private int tiro;
    private int cruzamento;
    private int giro;
    private int manuseioDeBola;
    private int forcaChute;
    private int gerenciamentoDeGols;
    private int jogoOfensivo;
    private int jogoDefensivo;
    public RelatorioDesempenhoResponse(com.br.Entity.RelatorioDesempenho entity) {
        this.id = entity.getId();
        this.controle = entity.getControle();
        this.recepcao = entity.getRecepcao();
        this.dribles = entity.getDribles();
        this.passe = entity.getPasse();
        this.tiro = entity.getTiro();
        this.cruzamento = entity.getCruzamento();
        this.giro = entity.getGiro();
        this.manuseioDeBola = entity.getManuseioDeBola();
        this.forcaChute = entity.getForcaChute();
        this.gerenciamentoDeGols = entity.getGerenciamentoDeGols();
        this.jogoOfensivo = entity.getJogoOfensivo();
        this.jogoDefensivo = entity.getJogoDefensivo();
    }
}
