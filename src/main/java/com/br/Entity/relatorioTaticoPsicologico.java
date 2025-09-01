package com.br.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Importar
import com.fasterxml.jackson.annotation.JsonBackReference; // Importar

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "relatorio_tatico")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Adicionado aqui
public class relatorioTaticoPsicologico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relatorio_avaliacao_geral_id", unique = true, nullable = false)
    @JsonBackReference // Indicar que este é o lado "back" do relacionamento, para evitar ciclo com RelatorioAvaliacaoGeral
    private relatorioAvaliacaoGeral relatorioAvaliacaoGeral;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atleta_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "comunicadosRecebidos", "relatoriosDesempenho", "relatoriosTaticoPsicologico"}) // Adicionado para evitar ciclos ou proxies não serializáveis do Atleta
    // As propriedades "comunicadosRecebidos", "relatoriosDesempenho", "relatoriosTaticoPsicologico" são inferidas se Atleta as tiver como OneToMany/ManyToMany
    private atleta atleta;

    @Column(name="esportividade")
    private int esportividade;

    @Column(name="disciplina")
    private int disciplina;

    @Column(name="foco")
    private int foco;

    @Column(name="confianca")
    private int confianca;

    @Column(name="tomada_de_decisoes")
    private int tomadaDecisoes;

    @Column(name="compromisso")
    private int compromisso;

    @Column(name="lideranca")
    private int lideranca;

    @Column(name="trabalho_em_equipe")
    private int trabalhoEmEquipe;

    @Column(name="atributos_fisicos")
    private int atributosFisicos;

    @Column(name="atuar_sob_pressao")
    private int atuarSobPressao;
}