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
import org.springframework.web.reactive.function.client.WebClientResponseException;
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
    private final analiseIaRepository analiseIaRepository;

    // SEU PROMPT FINAL: Usado para definir a persona e as regras de formato
    private static final String SYSTEM_INSTRUCTION_PROMPT =
            "Você é um Treinador e Analista de Desenvolvimento de Futebol (Base). Seu papel é ser um mentor honesto, motivador e profissional, cujo foco é maximizar o potencial de atletas jovens em formação (categorias de base).\n\n" +
                    "Sua análise deve ser totalmente baseada nos dados brutos de avaliação fornecidos pelo usuário, **indo direto ao ponto**. Não apresente os dados da avaliação em forma de lista; em vez disso, **descreva e analise o significado deles**.\n\n" +
                    "Tom e Linguagem: A análise deve ser construtiva, direta, mas sempre encorajadora. Use uma linguagem acessível e didática (como um professor/treinador). O objetivo é inspirar o atleta a melhorar e a valorizar o que ele já faz bem.\n\n" +
                    "Formato de Saída (Obrigatório):\n" +
                    "A resposta deve ser organizada de forma clara e profissional, utilizando listas com marcadores (• ou -) e quebras de linha para estruturar as informações. Siga exatamente estas cinco seções:\n\n" +
                    "1. Pontos Fortes e Consistência :\n" +
                    "   • Destaque 3 a 5 qualidades já consolidadas e use os dados históricos para comprovar a consistência ou tendência de melhoria.\n" +
                    "   • Inclua um breve texto sobre como manter e consolidar esses pontos fortes através de repetição.\n\n" +
                    "2. Foco para Aprimoramento :\n" +
                    "   • Identifique 3 a 5 áreas específicas (técnicas, táticas, psicológicas ou físicas) que, se desenvolvidas, trarão maior impacto no seu jogo atual e futuro. Seja específico e direto.\n\n" +
                    "3. Estratégia e Treinamento (Ações Práticas):\n" +
                    "   • Para cada Área de Aprimoramento do ponto 2, sugira 1 a 2 exercícios ou hábitos de treino práticos e concretos.\n" +
                    "   • As ações devem ser realistas e aplicáveis à rotina de um atleta de base.\n\n" +
                    "4. Visão de Futuro (Previsão Realista):\n" +
                    "   • Apresente uma projeção realista do nível que o atleta pode alcançar nos próximos 2 a 5 meses, vinculando o sucesso diretamente ao seu compromisso com a Estratégia e Treinamento propostos.\n\n" +
                    "5. Mensagem Final do Professor (Motivação):\n" +
                    "   • Encerre a análise com uma mensagem pessoal, concisa e de alto impacto que reforce a crença no potencial do atleta e o inspire a continuar o trabalho duro.";


    public geminiAnalysisService(WebClient.Builder webClientBuilder,
                                 ObjectMapper objectMapper,
                                 @Value("${gemini.api.key}") String geminiApiKey,
                                 @Value("${gemini.api.baseurl}") String geminiApiBaseUrl,
                                 @Value("${gemini.api.model}") String geminiApiModel,
                                 analiseIaRepository analiseIaRepository) {
        this.geminiApiKey = geminiApiKey;
        this.geminiApiBaseUrl = geminiApiBaseUrl;
        this.geminiApiModel = geminiApiModel;
        this.webClient = webClientBuilder.baseUrl(this.geminiApiBaseUrl).build();
        this.objectMapper = objectMapper;
        this.analiseIaRepository = analiseIaRepository;
    }

    public static class EvaluationScores {
        // ... (Corpo da classe EvaluationScores permanece o mesmo) ...
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

        // USER PROMPT: Combina a instrução de sistema com os dados para evitar o erro 400
        String userPromptData = String.format(
                "%s\n\n--- INÍCIO DOS DADOS DO ATLETA %s ---\n\n" +
                        "Histórico de Avaliações:\n%s\n" +
                        "--- Avaliação Mais Recente (%s) ---\n" +
                        "Scores de Desempenho: %s\n" +
                        "Scores Tático/Psicológico/Físico: %s\n" +
                        "Feedback Qualitativo Recente: \"%s\"",
                SYSTEM_INSTRUCTION_PROMPT, // Instrução de Sistema no início do prompt de dados
                atletaName,
                historicalData.toString(),
                latestEvaluation.date.format(formatter),
                mapToString(latestEvaluation.performanceScores),
                mapToString(latestEvaluation.tacticalPsychologicalScores),
                latestEvaluation.combinedFeedback.isEmpty() ? "N/A" : latestEvaluation.combinedFeedback
        );


        // -------------------------------------------------------------------------
        // MONTAGEM DO REQUEST BODY CORRIGIDO (Remoção do nó 'systemInstruction' para evitar o 400)
        // -------------------------------------------------------------------------

        ObjectNode requestBody = objectMapper.createObjectNode();

        // **A CORREÇÃO:** O nó systemInstruction foi removido. O prompt agora contém a instrução.
        // requestBody.put("systemInstruction", SYSTEM_INSTRUCTION_PROMPT); // <--- REMOVIDO

        // 1. Adicionar o Conteúdo (Dados de entrada, que agora incluem a instrução)
        ArrayNode contentsArray = requestBody.putArray("contents");
        ObjectNode contentObject = contentsArray.addObject();
        ArrayNode partsArray = contentObject.putArray("parts");
        partsArray.addObject().put("text", userPromptData); // Usa o prompt combinado

        // 2. Configurações de Geração (Aumento do maxOutputTokens)
        ObjectNode generationConfig = requestBody.putObject("generationConfig");
        generationConfig.put("temperature", 0.7);
        generationConfig.put("maxOutputTokens", 2048); // Aumentado para evitar corte
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

                        analiseIa novaAnalise = new analiseIa(atletaEmail, userPromptData, respostaDaIa);
                        analiseIaRepository.save(novaAnalise);

                        return respostaDaIa;
                    } else {
                        logger.error("Estrutura da resposta da IA inesperada. Resposta completa: {}", responseNode.toPrettyString());
                        throw new RuntimeException("Resposta da IA em formato inesperado.");
                    }
                })
                .onErrorResume(e -> {
                    // Tratamento para 403 Forbidden (chave inválida) e outros erros
                    if (e instanceof WebClientResponseException.Forbidden) {
                        logger.error("Erro 403 Forbidden na API Gemini. Verifique a API Key e permissões. Erro: {}", e.getMessage());
                        return Mono.error(new RuntimeException("Falha de Autenticação (403): Verifique a API Key do Gemini."));
                    }
                    if (e instanceof WebClientResponseException.BadRequest) {
                        logger.error("Erro 400 Bad Request na API Gemini. Verifique o formato JSON (provavelmente problema de token ou modelo/endpoint). Erro: {}", e.getMessage());
                        return Mono.error(new RuntimeException("Requisição Inválida (400): Erro no formato JSON ou na requisição para a IA."));
                    }

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