package com.br.Controller;

import com.br.Entity.Atleta;
import com.br.Entity.RelatorioAvaliacaoGeral;
import com.br.Repository.RelatorioAvaliacaoGeralRepository;
import com.br.Repository.AtletaRepository;
import com.br.Security.CustomUserDetails;
import com.br.Service.GeminiAnalysisService;
import com.br.Service.GeminiAnalysisService.EvaluationScores;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analise")
public class AthleteAnalysisController {

    private final RelatorioAvaliacaoGeralRepository relatorioAvaliacaoGeralRepository;
    private final GeminiAnalysisService geminiAnalysisService;
    private final AtletaRepository atletaRepository;

    public AthleteAnalysisController(RelatorioAvaliacaoGeralRepository relatorioAvaliacaoGeralRepository,
                                     GeminiAnalysisService geminiAnalysisService,
                                     AtletaRepository atletaRepository) {
        this.relatorioAvaliacaoGeralRepository = relatorioAvaliacaoGeralRepository;
        this.geminiAnalysisService = geminiAnalysisService;
        this.atletaRepository = atletaRepository;
    }

    @GetMapping("/meu-desempenho")
    public Mono<ResponseEntity<Map<String, Object>>> getMyComprehensiveAnalysis() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Mono.just(ResponseEntity.status(401).body(Map.of("error", "Não autenticado.")));
        }

        Long atletaId;
        String atletaNome;

        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            atletaId = userDetails.getId();
            atletaNome = userDetails.getUserName();
        } else {
            System.err.println("DEBUG Backend: Tipo de principal inesperado. Esperado CustomUserDetails, mas obteve: " + authentication.getPrincipal().getClass().getName());
            return Mono.just(ResponseEntity.status(500).body(Map.of("error", "Erro interno ao processar credenciais do usuário. Tipo de principal inesperado.")));
        }

        System.out.println("DEBUG Backend: Buscando análises para o atleta ID logado: " + atletaId + " (Nome: " + atletaNome + ")");

        List<RelatorioAvaliacaoGeral> evaluations = relatorioAvaliacaoGeralRepository.findAllByAtletaIdOrderByDataAvaliacaoAsc(atletaId);

        if (evaluations.isEmpty()) {
            return Mono.just(ResponseEntity.ok(Map.of(
                    "atletaId", atletaId,
                    "nomeAtleta", atletaNome,
                    "comprehensiveAnalysis", "Nenhuma avaliação de desempenho encontrada para este atleta. Converse com seu treinador para iniciar."
            )));
        }

        List<EvaluationScores> evaluationScoresList = evaluations.stream()
                .map(EvaluationScores::new)
                .collect(Collectors.toList());

        String atletaNameForGemini = Optional.ofNullable(evaluations.get(0).getAtleta())
                .map(Atleta::getNome)
                .orElse(atletaNome);

        return geminiAnalysisService.generateComprehensiveAnalysis(atletaNameForGemini, evaluationScoresList)
                .map(geminiResponse -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("atletaId", atletaId);
                    response.put("nomeAtleta", atletaNameForGemini);
                    response.put("comprehensiveAnalysis", geminiResponse);
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(e -> {
                    System.err.println("Erro ao gerar análise completa do atleta: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(500).body(Map.of("error", "Erro interno ao gerar análise completa da IA: " + e.getMessage())));
                })
                .defaultIfEmpty(ResponseEntity.status(500).body(Map.of("error", "Análise do Gemini não retornou nada inesperadamente.")));
    }
}