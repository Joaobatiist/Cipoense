package com.br.Service;

import com.br.Entity.analiseIa;
import com.br.Entity.relatorioAvaliacaoGeral;
import com.br.Entity.relatorioDesempenho;
import com.br.Entity.relatorioTaticoPsicologico;
import com.br.Repository.analiseIaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class geminiAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(geminiAnalysisService.class);

    private final String geminiApiKey;
    private final String geminiApiBaseUrl;
    private final String geminiApiModel;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final analiseIaRepository analiseIaRepository; // NOVO: Injeção do Repository

    public geminiAnalysisService(WebClient.Builder webClientBuilder,
                                 ObjectMapper objectMapper,
                                 @Value("${gemini.api.key}") String geminiApiKey,
                                 @Value("${gemini.api.baseurl}") String geminiApiBaseUrl,
                                 @Value("${gemini.api.model}") String geminiApiModel,
                                 analiseIaRepository analiseIaRepository) { // NOVO: Injeção por construtor
        this.geminiApiKey = geminiApiKey;
        this.geminiApiBaseUrl = geminiApiBaseUrl;
        this.geminiApiModel = geminiApiModel;
        this.webClient = webClientBuilder.baseUrl(this.geminiApiBaseUrl).build();
        this.objectMapper = objectMapper;
        this.analiseIaRepository = analiseIaRepository; // NOVO: Atribuição do Repository
    }

    public static class EvaluationScores {
        // ... (conteúdo da classe é o mesmo) ...
        public LocalDate date;
        public Map<String, Integer> performanceScores = new HashMap<>();
        public Map<String, Integer> tacticalPsychologicalScores = new HashMap<>();
        public String combinedFeedback;

        private static final Map<String, String> PERFORMANCE_ATTR_MAP = new HashMap<>();
        static {
            PERFORMANCE_ATTR_MAP.put("controle", "Controle de Bola");
            PERFORMANCE_ATTR_MAP.put("recepcao", "Recepção");
            PERFORMANCE_ATTR_MAP.put("dribles", "Dribles");
            PERFORMANCE_ATTR_MAP.put("passe", "Passe");
            PERFORMANCE_ATTR_MAP.put("tiro", "Tiro");
            PERFORMANCE_ATTR_MAP.put("cruzamento", "Cruzamento");
            PERFORMANCE_ATTR_MAP.put("giro", "Giro");
            PERFORMANCE_ATTR_MAP.put("manuseioDeBola", "Manuseio de Bola");
            PERFORMANCE_ATTR_MAP.put("forcaChute", "Força no Chute");
            PERFORMANCE_ATTR_MAP.put("gerenciamentoDeGols", "Gerenciamento de Gols");
            PERFORMANCE_ATTR_MAP.put("jogoOfensivo", "Jogo Ofensivo");
            PERFORMANCE_ATTR_MAP.put("jogoDefensivo", "Jogo Defensivo");
        }

        private static final Map<String, String> TACTICAL_PSYCH_ATTR_MAP = new HashMap<>();
        static {
            TACTICAL_PSYCH_ATTR_MAP.put("esportividade", "Esportividade");
            TACTICAL_PSYCH_ATTR_MAP.put("disciplina", "Disciplina");
            TACTICAL_PSYCH_ATTR_MAP.put("foco", "Foco");
            TACTICAL_PSYCH_ATTR_MAP.put("confianca", "Confiança");
            TACTICAL_PSYCH_ATTR_MAP.put("tomadaDecisoes", "Tomada de Decisões");
            TACTICAL_PSYCH_ATTR_MAP.put("compromisso", "Compromisso");
            TACTICAL_PSYCH_ATTR_MAP.put("lideranca", "Liderança");
            TACTICAL_PSYCH_ATTR_MAP.put("trabalhoEmEquipe", "Trabalho em Equipe");
            TACTICAL_PSYCH_ATTR_MAP.put("atributosFisicos", "Atributos Físicos");
            TACTICAL_PSYCH_ATTR_MAP.put("atuarSobPressao", "Atuar Sob Pressão");
        }

        public EvaluationScores(relatorioAvaliacaoGeral evaluation) {
            this.date = evaluation.getDataAvaliacao();

            Optional.ofNullable(evaluation.getRelatorioDesempenho())
                    .ifPresent(rd -> PERFORMANCE_ATTR_MAP.forEach((key, readableName) -> {
                        try {
                            Integer score = (Integer) relatorioDesempenho.class.getMethod("get" + capitalize(key)).invoke(rd);
                            performanceScores.put(readableName, score);
                        } catch (Exception e) {
                            System.err.println("Erro ao obter score de desempenho para " + key + ": " + e.getMessage());
                        }
                    }));

            Optional.ofNullable(evaluation.getRelatorioTaticoPsicologico())
                    .ifPresent(rtp -> TACTICAL_PSYCH_ATTR_MAP.forEach((key, readableName) -> {
                        try {
                            Integer score = (Integer) relatorioTaticoPsicologico.class.getMethod("get" + capitalize(key)).invoke(rtp);
                            tacticalPsychologicalScores.put(readableName, score);
                        } catch (Exception e) {
                            System.err.println("Erro ao obter score tático/psicológico para " + key + ": " + e.getMessage());
                        }
                    }));

            this.combinedFeedback = Stream.of(
                            evaluation.getFeedbackTreinador(),
                            evaluation.getFeedbackAvaliador(),
                            evaluation.getPontosFortes(),
                            evaluation.getPontosFracos(),
                            evaluation.getAreasAprimoramento(),
                            evaluation.getMetasObjetivos()
                    )
                    .filter(s -> s != null && !s.trim().isEmpty())
                    .collect(Collectors.joining("\n\n"));
        }

        private String capitalize(String str) {
            if (str == null || str.isEmpty()) {
                return str;
            }
            return Character.toUpperCase(str.charAt(0)) + str.substring(1);
        }
    }

    public Mono<String> generateComprehensiveAnalysis(String atletaName, List<EvaluationScores> evaluations, String atletaEmail) {
        if (evaluations.isEmpty()) {
            return Mono.just("Não há dados de avaliação para gerar uma análise abrangente para " + atletaName + ".");
        }

        EvaluationScores latestEvaluation = evaluations.stream()
                .max(Comparator.comparing(e -> e.date))
                .orElseThrow(() -> new IllegalStateException("Nenhuma avaliação encontrada na lista."));

        StringBuilder historicalData = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        int historyLimit = Math.min(evaluations.size(), 5);
        List<EvaluationScores> recentHistoricalEvaluations = evaluations.subList(Math.max(0, evaluations.size() - historyLimit), evaluations.size());

        for (EvaluationScores eval : recentHistoricalEvaluations) {
            historicalData.append("--- Avaliação em ").append(eval.date.format(formatter)).append(" ---\n");
            historicalData.append("Desempenho: ").append(mapToString(eval.performanceScores)).append("\n");
            historicalData.append("Tático/Psicológico/Físico: ").append(mapToString(eval.tacticalPsychologicalScores)).append("\n");
            if (!eval.combinedFeedback.isEmpty()) {
                historicalData.append("Feedback Qualitativo: \"").append(eval.combinedFeedback).append("\"\n");
            }
        }
        historicalData.append("\n");

        String promptText = String.format(
                "Você é um treinador de futebol inteligente e experiente. Sua tarefa é analisar o desempenho do atleta %s. " +
                        "A análise deve ser para que o atleta melhora e se mantenha, com um tom de voz claro e de treinador. " +
                        "A resposta deve ser organizada em seções claras, sem usar negrito em qualquer parte do texto. Use parágrafos ou marcadores simples para cada item. " +
                        "Use as seguintes informações para a sua análise: \n\n" +
                        "Dados Históricos: %s\n" +
                        "--- Avaliação Mais Recente (%s) ---\n" +
                        "Desempenho: %s\n" +
                        "Tático/Psicológico/Físico: %s\n" +
                        "Feedback Qualitativo Recente: \"%s\"\n\n" +
                        "Com base nesses dados, forneça uma análise detalhada, focando nos seguintes pontos:\n\n" +
                        "1. Pontos Fortes:\n" +
                        "- Destaque 3 a 5 pontos fortes claros do atleta, com exemplos ou tendências de melhoria ao longo do tempo.\n\n" +
                        "2. Áreas de Aprimoramento:\n" +
                        "- Identifique 3 a 5 áreas específicas onde o atleta pode melhorar. Seja direto e construtivo. " +
                        "Inclua  treino específicas para oque o pode melhorar atleta.\n\n" +
                        "3. Plano de Ação:\n" +
                        "- Para cada área de aprimoramento, sugira 1-2 ações práticas e concretas que o atleta pode realizar nos treinos e jogos para desenvolver essas habilidades.\n\n" +
                        "4. Previsão de Desempenho:\n" +
                        "- Ofereça uma previsão realista do que o atleta pode alcançar se esforçar mais, baseando-se nas tendências e no compromisso com o plano de ação.\n\n" +
                        "5. Mensagem Final:\n" +
                        "- Escreva uma mensagem de encorajamento, finalizando a análise." ,
                atletaName,
                atletaName,
                historicalData.toString(),
                latestEvaluation.date.format(formatter),
                mapToString(latestEvaluation.performanceScores),
                mapToString(latestEvaluation.tacticalPsychologicalScores),
                latestEvaluation.combinedFeedback.isEmpty() ? "N/A" : latestEvaluation.combinedFeedback
        );

        // A lógica de salvamento será adicionada no método map para processar a resposta
        // O restante do código de requisição permanece o mesmo

        ObjectNode requestBody = objectMapper.createObjectNode();
        ArrayNode contentsArray = requestBody.putArray("contents");
        ObjectNode contentObject = contentsArray.addObject();
        ArrayNode partsArray = contentObject.putArray("parts");
        partsArray.addObject().put("text", promptText);

        ObjectNode generationConfig = requestBody.putObject("generationConfig");
        generationConfig.put("temperature", 0.7);
        generationConfig.put("maxOutputTokens", 800);
        generationConfig.put("topP", 0.9);
        generationConfig.put("topK", 40);

        String apiPath = "/models/{modelId}:generateContent";
        logger.info("GeminiAnalysisService: Chamando Gemini API. Caminho relativo: {} com model: {}", apiPath, geminiApiModel);

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(apiPath)
                        .queryParam("key", geminiApiKey)
                        .build(geminiApiModel))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(responseNode -> {
                    JsonNode textNode = responseNode
                            .path("candidates")
                            .path(0)
                            .path("content")
                            .path("parts")
                            .path(0)
                            .path("text");
                    if (textNode.isTextual()) {
                        String respostaDaIa = textNode.asText();

                        // NOVO: Criar e salvar a entidade AnaliseIa
                        analiseIa novaAnalise = new analiseIa(atletaEmail, promptText, respostaDaIa);
                        analiseIaRepository.save(novaAnalise);

                        return respostaDaIa;
                    } else {
                        logger.error("Estrutura da resposta da IA inesperada. Resposta completa: {}", responseNode.toPrettyString());
                        throw new RuntimeException("Resposta da IA em formato inesperado.");
                    }
                })
                .onErrorResume(e -> {
                    logger.error("Erro na chamada à API do Google Gemini (WebClient): {}", e.getMessage(), e);
                    return Mono.error(new RuntimeException("Não foi possível gerar a análise completa (erro de comunicação com a IA).", e));
                });
    }

    private String mapToString(Map<String, Integer> map) {
        return map.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + (entry.getValue() != null ? entry.getValue() : "N/A"))
                .collect(Collectors.joining(", "));
    }
}