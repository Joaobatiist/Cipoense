package com.br.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "relatorio_tatico") // Nome da tabela no banco de dados
public class RelatorioTaticoPsicologico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Chave primária para esta tabela

    // Relacionamento One-to-One com RelatorioAvaliacaoGeral
    // @JoinColumn indica a coluna de chave estrangeira nesta tabela
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relatorio_avaliacao_geral_id", unique = true, nullable = false)
    private RelatorioAvaliacaoGeral relatorioAvaliacaoGeral;

    // Relacionamento Many-to-One com Atleta (um relatório tático/psicológico pertence a um atleta)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atleta_id", nullable = false) // Coluna de chave estrangeira na tabela relatorio_tatico
    private Atleta atleta;

    // --- Campos de Avaliação Tática/Psicológica/Física ---
    @Column(name="esportividade")
    private int esportividade; // Nível de esportividade

    @Column(name="disciplina")
    private int disciplina; // Nível de disciplina

    @Column(name="foco")
    private int foco; // Nível de foco

    @Column(name="confianca")
    private int confianca; // Nível de confiança

    @Column(name="tomada_de_decisoes")
    private int tomadaDecisoes; // Habilidade de tomada de decisões

    @Column(name="compromisso")
    private int compromisso; // Nível de compromisso

    @Column(name="lideranca")
    private int lideranca; // Habilidade de liderança

    @Column(name="trabalho_em_equipe")
    private int trabalhoEmEquipe; // Habilidade de trabalho em equipe

    @Column(name="atributos_fisicos")
    private int atributosFisicos; // Avaliação de atributos físicos

    @Column(name="atuar_sob_pressao")
    private int atuarSobPressao; // Capacidade de atuar sob pressão

}
