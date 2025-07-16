package com.br.Entity;

import com.br.Enums.SubDivisao;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "relatorio_avaliacao_geral") // Nome da tabela no banco de dados
public class RelatorioAvaliacaoGeral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atleta_id")
    @Nullable
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

    // --- Relacionamentos com os relatórios detalhados (OneToOne) ---
    // Mapeia para as entidades de relatório de desempenho e tático/psicológico
    // mappedBy indica que a outra entidade (RelatorioDesempenho) é dona do relacionamento
    @OneToOne(mappedBy = "relatorioAvaliacaoGeral", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private RelatorioDesempenho relatorioDesempenho;

    // mappedBy indica que a outra entidade (RelatorioTaticoPsicologico) é dona do relacionamento
    @OneToOne(mappedBy = "relatorioAvaliacaoGeral", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private RelatorioTaticoPsicologico relatorioTaticoPsicologico;

    // --- Métodos de Conveniência para setar Relatórios Detalhados (para bidirecionalidade) ---
    // Garante que a referência inversa seja setada corretamente
    public void setRelatorioDeDesempenho(RelatorioDesempenho relatorioDesempenho) {
        if (relatorioDesempenho != null) {
            relatorioDesempenho.setRelatorioAvaliacaoGeral(this); // Seta a referência de volta na entidade filha
        }
        this.relatorioDesempenho = relatorioDesempenho;
    }

    // Garante que a referência inversa seja setada corretamente
    public void setRelatorioTaticoPsicologico(RelatorioTaticoPsicologico relatorioTaticoPsicologico) {
        if (relatorioTaticoPsicologico != null) {
            relatorioTaticoPsicologico.setRelatorioAvaliacaoGeral(this); // Seta a referência de volta na entidade filha
        }
        this.relatorioTaticoPsicologico = relatorioTaticoPsicologico;
    }
}