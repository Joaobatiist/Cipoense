package com.br.Entity;

import com.br.Enums.posicao;
import com.br.Enums.subDivisao;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Importar
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "relatorio_avaliacao_geral")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Adicionado aqui
public class relatorioAvaliacaoGeral {

    @Id
    @UuidGenerator
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atleta_id")
    @Nullable
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "comunicadosRecebidos", "relatoriosDesempenho", "relatoriosTaticoPsicologico"}) // Adicionado para evitar ciclos ou proxies não serializáveis do Atleta
    // A propriedade "comunicadosRecebidos" aqui é baseada no seu log anterior. Se Atleta não tiver isso, pode remover.
    // As propriedades "relatoriosDesempenho" e "relatoriosTaticoPsicologico" são inferidas se Atleta as tiver como OneToMany/ManyToMany
    private atleta atleta;

    @Enumerated(EnumType.STRING)
    private subDivisao subDivisao;

    @Enumerated(EnumType.STRING)
    private posicao posicao;

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
    private relatorioDesempenho relatorioDesempenho;

    @OneToOne(mappedBy = "relatorioAvaliacaoGeral", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "relatorioAvaliacaoGeral", "atleta"}) // Adicionado aqui, ignorando o próprio relacionamento reverso
    private relatorioTaticoPsicologico relatorioTaticoPsicologico;

    public void setRelatorioDeDesempenho(relatorioDesempenho relatorioDesempenho) {
        if (relatorioDesempenho != null) {
            relatorioDesempenho.setRelatorioAvaliacaoGeral(this);
        }
        this.relatorioDesempenho = relatorioDesempenho;
    }

    public void setRelatorioTaticoPsicologico(relatorioTaticoPsicologico relatorioTaticoPsicologico) {
        if (relatorioTaticoPsicologico != null) {
            relatorioTaticoPsicologico.setRelatorioAvaliacaoGeral(this);
        }
        this.relatorioTaticoPsicologico = relatorioTaticoPsicologico;
    }
}