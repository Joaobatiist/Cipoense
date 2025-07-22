package com.br.Entity;

import com.br.Enums.SubDivisao;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Importar

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "relatorio_avaliacao_geral")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Adicionado aqui
public class RelatorioAvaliacaoGeral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atleta_id")
    @Nullable
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "comunicadosRecebidos", "relatoriosDesempenho", "relatoriosTaticoPsicologico"}) // Adicionado para evitar ciclos ou proxies não serializáveis do Atleta
    // A propriedade "comunicadosRecebidos" aqui é baseada no seu log anterior. Se Atleta não tiver isso, pode remover.
    // As propriedades "relatoriosDesempenho" e "relatoriosTaticoPsicologico" são inferidas se Atleta as tiver como OneToMany/ManyToMany
    private Atleta atleta;

    @Enumerated(EnumType.STRING)
    private SubDivisao subDivisao;

    private String userName;

    private LocalDate dataAvaliacao;

    private String periodoTreino;

    @Column(columnDefinition = "TEXT")
    private String feedbackTreinador;

    @Column(columnDefinition = "TEXT")
    private String feedbackAvaliador;

    @Column(columnDefinition = "TEXT")
    private String pontosFortes;

    @Column(columnDefinition = "TEXT")
    private String pontosFracos;

    @Column(columnDefinition = "TEXT")
    private String areasAprimoramento;

    @Column(columnDefinition = "TEXT")
    private String metasObjetivos;

    @OneToOne(mappedBy = "relatorioAvaliacaoGeral", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "relatorioAvaliacaoGeral", "atleta"}) // Adicionado aqui, ignorando o próprio relacionamento reverso
    private RelatorioDesempenho relatorioDesempenho;

    @OneToOne(mappedBy = "relatorioAvaliacaoGeral", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "relatorioAvaliacaoGeral", "atleta"}) // Adicionado aqui, ignorando o próprio relacionamento reverso
    private RelatorioTaticoPsicologico relatorioTaticoPsicologico;

    public void setRelatorioDeDesempenho(RelatorioDesempenho relatorioDesempenho) {
        if (relatorioDesempenho != null) {
            relatorioDesempenho.setRelatorioAvaliacaoGeral(this);
        }
        this.relatorioDesempenho = relatorioDesempenho;
    }

    public void setRelatorioTaticoPsicologico(RelatorioTaticoPsicologico relatorioTaticoPsicologico) {
        if (relatorioTaticoPsicologico != null) {
            relatorioTaticoPsicologico.setRelatorioAvaliacaoGeral(this);
        }
        this.relatorioTaticoPsicologico = relatorioTaticoPsicologico;
    }
}