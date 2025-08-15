package com.br.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Table
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class AnaliseIa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "atleta_email", nullable = false)
    private String atletaEmail;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String prompt;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String respostaIA;

    @Column(name = "data_analise", nullable = false)
    private LocalDateTime dataAnalise;

    // Construtor manual para a lógica do GeminiAnalysisService
    public AnaliseIa(String atletaEmail, String prompt, String respostaIA) {
        this.atletaEmail = atletaEmail;
        this.prompt = prompt;
        this.respostaIA = respostaIA;
        this.dataAnalise = LocalDateTime.now();
    }
}