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
@Table(name = "relatorio_tecnico")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Adicionado aqui
public class relatorioDesempenho {
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

    @Column(name="controle")
    private int controle;

    @Column(name="recepcao")
    private int recepcao;

    @Column(name="dribles")
    private int dribles;

    @Column(name="passe")
    private int passe;

    @Column(name="tiro")
    private int tiro;

    @Column(name="cruzamento")
    private int cruzamento;

    @Column(name="giro")
    private int giro;

    @Column(name="manuseio_de_bola")
    private int manuseioDeBola;

    @Column(name="forca_chute")
    private int forcaChute;

    @Column(name="gerenciamento_de_gols")
    private int gerenciamentoDeGols;

    @Column(name="jogo_ofensivo")
    private int jogoOfensivo;

    @Column(name="jogo_defensivo")
    private int jogoDefensivo;
}