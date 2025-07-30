package com.br.Service;

import com.br.Entity.RelatorioDesempenho;
import com.br.Entity.RelatorioAvaliacaoGeral;
import com.br.Entity.RelatorioTaticoPsicologico;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GeminiAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiAnalysisService.class);

    // Estas variáveis são agora 'final' porque serão inicializadas no construtor
    private final String geminiApiKey;
    private final String geminiApiBaseUrl;
    private final String geminiApiModel;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    // O CONSTRUTOR CORRIGIDO:
    // As variáveis com @Value são injetadas diretamente nos parâmetros do construtor.
    // Isso garante que elas tenham valor ANTES de serem usadas para construir o WebClient.
    public GeminiAnalysisService(WebClient.Builder webClientBuilder,
                                 ObjectMapper objectMapper,
                                 @Value("${gemini.api.key}") String geminiApiKey,
                                 @Value("${gemini.api.baseurl}") String geminiApiBaseUrl,
                                 @Value("${gemini.api.model}") String geminiApiModel) {
        // Atribua os valores injetados aos campos da classe
        this.geminiApiKey = geminiApiKey;
        this.geminiApiBaseUrl = geminiApiBaseUrl;
        this.geminiApiModel = geminiApiModel;

        // Adicione logs para confirmar que os valores foram injetados corretamente
        logger.info("GeminiAnalysisService: Inicializando com a Base URL: {}", this.geminiApiBaseUrl);
        logger.info("GeminiAnalysisService: Modelo da API: {}", this.geminiApiModel);
        // Log parcial da chave para segurança, não exponha a chave completa em logs
        logger.info("GeminiAnalysisService: Chave da API (parcial): {}...",
                this.geminiApiKey.substring(0, Math.min(this.geminiApiKey.length(), 5)));

        // Agora, construa o WebClient usando a base URL que JÁ FOI INJETADA
        this.webClient = webClientBuilder.baseUrl(this.geminiApiBaseUrl).build();
        this.objectMapper = objectMapper;
    }

    // ⭐ Sua classe EvaluationScores (sem mudanças aqui) - MANTENHA ESTA CLASSE
    public static class EvaluationScores {
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

        public EvaluationScores(RelatorioAvaliacaoGeral evaluation) {
            this.date = evaluation.getDataAvaliacao();

            Optional.ofNullable(evaluation.getRelatorioDesempenho())
                    .ifPresent(rd -> PERFORMANCE_ATTR_MAP.forEach((key, readableName) -> {
                        try {
                            Integer score = (Integer) RelatorioDesempenho.class.getMethod("get" + capitalize(key)).invoke(rd);
                            performanceScores.put(readableName, score);
                        } catch (Exception e) {
                            System.err.println("Erro ao obter score de desempenho para " + key + ": " + e.getMessage());
                        }
                    }));

            Optional.ofNullable(evaluation.getRelatorioTaticoPsicologico())
                    .ifPresent(rtp -> TACTICAL_PSYCH_ATTR_MAP.forEach((key, readableName) -> {
                        try {
                            Integer score = (Integer) RelatorioTaticoPsicologico.class.getMethod("get" + capitalize(key)).invoke(rtp);
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

    // ⭐ O método principal generateComprehensiveAnalysis (sem mudanças significativas aqui, exceto o log)
    public Mono<String> generateComprehensiveAnalysis(String atletaName, List<EvaluationScores> evaluations) {
        if (evaluations.isEmpty()) {
            return Mono.just("Não há dados de avaliação para gerar uma análise abrangente para " + atletaName + ".");
        }

        EvaluationScores latestEvaluation = evaluations.stream()
                .max(Comparator.comparing(e -> e.date))
                .orElseThrow(() -> new IllegalStateException("Nenhuma avaliação encontrada na lista."));

        StringBuilder historicalData = new StringBuilder();
        historicalData.append("Histórico de Avaliações:\n");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        int historyLimit = Math.min(evaluations.size(), 5);
        List<EvaluationScores> recentHistoricalEvaluations = evaluations.subList(Math.max(0, evaluations.size() - historyLimit), evaluations.size() -1);

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
                "Você é um treinador de futebol inteligente e experiente. Sua tarefa é analisar o desempenho do atleta %s " +
                        "com base em suas avaliações históricas e na avaliação mais recente. " +
                        "Não envie os tópicos em negrito"+
                        "Forneça uma análise personalizada de acordo com a posiçao do atleta, levando em conta as principais caracteristicas de cada posição, instrutiva e motivacional, focando em:\n" +
                        "1. Pontos Fortes: Destaque 3 a 5 pontos fortes claros com exemplos ou tendências de melhoria ao longo do tempo.\n" +
                        "2. Áreas de Aprimoramento: Identifique 3 a 5 áreas específicas onde o atleta pode melhorar. Seja direto e construtivo.\n" +
                        "3. Como Melhorar (Plano de Ação): Para cada área de aprimoramento, sugira 1-2 ações práticas e concretas que o atleta pode realizar nos treinos e jogos para desenvolver essas habilidades.\n" +
                        "4. Previsão de Desempenho: Com base nas tendências observadas e no compromisso com o plano de ação, ofereça uma previsão realista (e encorajadora) do que o atleta pode alcançar em 3-6 meses. Evite promessas exageradas.\n" +
                        "5. Mensagem Final: Uma mensagem de encorajamento e inspiração.\n" +
                        "Use um tom de voz de treinador, motivador e claro. Formate a resposta de forma organizada com marcadores ou parágrafos claros para cada seção.\n" +
                        "%s" +
                        "--- Avaliação Mais Recente (%s) ---\n" +
                        "Desempenho: %s\n" +
                        "Tático/Psicológico/Físico: %s\n" +
                        "Feedback Qualitativo Recente: \"%s\"\n",
                atletaName,
                historicalData.toString(),
                latestEvaluation.date.format(formatter),
                mapToString(latestEvaluation.performanceScores),
                mapToString(latestEvaluation.tacticalPsychologicalScores),
                latestEvaluation.combinedFeedback.isEmpty() ? "N/A" : latestEvaluation.combinedFeedback
        );

        // Construir o corpo da requisição JSON conforme o formato da API Gemini (Google AI Studio)
        ObjectNode requestBody = objectMapper.createObjectNode();
        ArrayNode contentsArray = requestBody.putArray("contents");
        ObjectNode contentObject = contentsArray.addObject();
        ArrayNode partsArray = contentObject.putArray("parts");
        partsArray.addObject().put("text", promptText);

        // Adicionar parâmetros opcionais como temperatura, maxOutputTokens, etc.
        // Estes são passados no corpo da requisição para esta API
        ObjectNode generationConfig = requestBody.putObject("generationConfig");
        generationConfig.put("temperature", 0.7);
        generationConfig.put("maxOutputTokens", 800);
        generationConfig.put("topP", 0.9);
        generationConfig.put("topK", 40);

        // Fazer a chamada HTTP POST usando WebClient
        String apiPath = "/models/{modelId}:generateContent";
        logger.info("GeminiAnalysisService: Chamando Gemini API. Caminho relativo: {} com model: {}", apiPath, geminiApiModel); // Log do caminho e modelo

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(apiPath) // Caminho relativo, base URL já configurada
                        .queryParam("key", geminiApiKey) // A chave de API vai como query parameter para esta API
                        .build(geminiApiModel))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestBody)) // O corpo da requisição é o JSON que montamos
                .retrieve()
                .bodyToMono(JsonNode.class) // Espera a resposta JSON completa
                .map(responseNode -> {
                    // Parsear a resposta para extrair o texto gerado
                    JsonNode textNode = responseNode
                            .path("candidates")
                            .path(0)
                            .path("content")
                            .path("parts")
                            .path(0)
                            .path("text");

                    if (textNode.isTextual()) {
                        return textNode.asText();
                    } else {
                        logger.error("Estrutura da resposta da IA inesperada. Resposta completa: {}", responseNode.toPrettyString());
                        return "Resposta da IA em formato inesperado. Verifique logs.";
                    }
                })
                .onErrorResume(e -> {
                    logger.error("Erro na chamada à API do Google Gemini (WebClient): {}", e.getMessage(), e);
                    // Não use System.err.println em produção, prefira o logger.error
                    return Mono.just("Não foi possível gerar a análise completa (erro de comunicação com a IA).");
                });
    }

    private String mapToString(Map<String, Integer> map) {
        return map.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + (entry.getValue() != null ? entry.getValue() : "N/A"))
                .collect(Collectors.joining(", "));
    }
}