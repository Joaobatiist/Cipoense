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
@Table(name = "relatorio_tecnico") // Nome da tabela no banco de dados
public class RelatorioDesempenho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Chave primária para esta tabela

    // Relacionamento One-to-One com RelatorioAvaliacaoGeral
    // @JoinColumn indica a coluna de chave estrangeira nesta tabela
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relatorio_avaliacao_geral_id", unique = true, nullable = false)
    private RelatorioAvaliacaoGeral relatorioAvaliacaoGeral;

    // Relacionamento Many-to-One com Atleta (um relatório de desempenho pertence a um atleta)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atleta_id", nullable = false) // Coluna de chave estrangeira na tabela relatorio_tecnico
    private Atleta atleta;

    // --- Campos de Avaliação Técnica ---
    @Column(name="controle")
    private int controle; // Habilidade de controle de bola

    @Column(name="recepcao")
    private int recepcao; // Habilidade de recepção de bola

    @Column(name="dribles")
    private int dribles; // Habilidade de drible

    @Column(name="passe")
    private int passe; // Habilidade de passe

    @Column(name="tiro")
    private int tiro; // Habilidade de chute a gol

    @Column(name="cruzamento")
    private int cruzamento; // Habilidade de cruzamento

    @Column(name="giro")
    private int giro; // Habilidade de giro com a bola

    @Column(name="manuseio_de_bola") // Nome de coluna mais padronizado
    private int manuseioDeBola; // Habilidade de manuseio geral da bola

    @Column(name="forca_chute") // Nome de coluna mais padronizado
    private int forcaChute; // Força no chute

    @Column(name="gerenciamento_de_gols") // Nome de coluna mais padronizado
    private int gerenciamentoDeGols; // Gerenciamento de gols (para goleiros, ou situações de gol)

    @Column(name="jogo_ofensivo") // Nome de coluna mais padronizado
    private int jogoOfensivo; // Desempenho em situações ofensivas

    @Column(name="jogo_defensivo") // Nome de coluna mais padronizado
    private int jogoDefensivo; // Desempenho em situações defensivas

    // Métodos Getter e Setter gerados pelo Lombok (@Getter, @Setter)
}