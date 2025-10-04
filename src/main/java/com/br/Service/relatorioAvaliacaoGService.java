package com.br.Service;

import com.br.Entity.*;
import com.br.Repository.atletaRepository;
import com.br.Repository.relatorioAvaliacaoGeralRepository;
import com.br.Request.criarAvaliacaoRequest;
import com.br.Response.avaliacaoGeralResponse;
import com.br.Response.relatorioDesempenhoResponse;
import com.br.Response.relatorioTaticoPsicologicoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class relatorioAvaliacaoGService {

    private final relatorioAvaliacaoGeralRepository relatorioAvaliacaoGeralRepository;
    private final atletaRepository atletaRepository;
    private final geminiAnalysisService geminiAnalysisService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Autowired
    public relatorioAvaliacaoGService(
            relatorioAvaliacaoGeralRepository relatorioAvaliacaoGeralRepository,
            atletaRepository atletaRepository,
            geminiAnalysisService geminiAnalysisService) {
        this.relatorioAvaliacaoGeralRepository = relatorioAvaliacaoGeralRepository;
        this.atletaRepository = atletaRepository;
        this.geminiAnalysisService = geminiAnalysisService;
    }

    @Transactional
    public avaliacaoGeralResponse cadastrarRelatorioGeral(criarAvaliacaoRequest request) {
        if (request.getAtletaId() == null) {
            throw new IllegalArgumentException("ID do Atleta é obrigatório para cadastrar o relatório geral.");
        }
        atleta atleta = atletaRepository.findById(request.getAtletaId())
                .orElseThrow(() -> new RuntimeException("Atleta com ID " + request.getAtletaId() + " não encontrado."));

        // Lógica de criação do RelatorioAvaliacaoGeral (sem alterações aqui)
        relatorioAvaliacaoGeral relatorioGeral = new relatorioAvaliacaoGeral();
        relatorioGeral.setAtleta(atleta);
        relatorioGeral.setUserName(request.getUserName());

        if (atleta.getSubDivisao() != null) {
            relatorioGeral.setSubDivisao(atleta.getSubDivisao());
        }
        if (atleta.getPosicao() != null) {
            relatorioGeral.setPosicao(atleta.getPosicao());
        }
        relatorioGeral.setDataAvaliacao(request.getDataAvaliacao());
        relatorioGeral.setPeriodoTreino(request.getPeriodoTreino());
        relatorioGeral.setFeedbackTreinador(request.getFeedbackTreinador());
        relatorioGeral.setFeedbackAvaliador(request.getFeedbackAvaliador());
        relatorioGeral.setPontosFortes(request.getPontosFortes());
        relatorioGeral.setPontosFracos(request.getPontosFracos());
        relatorioGeral.setAreasAprimoramento(request.getAreasAprimoramento());
        relatorioGeral.setMetasObjetivos(request.getMetasObjetivos());

        if (request.getRelatorioDesempenho() != null) {
            relatorioDesempenho relatorioDesempenho = new relatorioDesempenho();
            relatorioDesempenho.setControle(request.getRelatorioDesempenho().getControle());
            relatorioDesempenho.setRecepcao(request.getRelatorioDesempenho().getRecepcao());
            relatorioDesempenho.setDribles(request.getRelatorioDesempenho().getDribles());
            relatorioDesempenho.setPasse(request.getRelatorioDesempenho().getPasse());
            relatorioDesempenho.setTiro(request.getRelatorioDesempenho().getTiro());
            relatorioDesempenho.setCruzamento(request.getRelatorioDesempenho().getCruzamento());
            relatorioDesempenho.setGiro(request.getRelatorioDesempenho().getGiro());
            relatorioDesempenho.setManuseioDeBola(request.getRelatorioDesempenho().getManuseioDeBola());
            relatorioDesempenho.setForcaChute(request.getRelatorioDesempenho().getForcaChute());
            relatorioDesempenho.setGerenciamentoDeGols(request.getRelatorioDesempenho().getGerenciamentoDeGols());
            relatorioDesempenho.setJogoOfensivo(request.getRelatorioDesempenho().getJogoOfensivo());
            relatorioDesempenho.setJogoDefensivo(request.getRelatorioDesempenho().getJogoDefensivo());
            relatorioDesempenho.setAtleta(atleta);
            relatorioGeral.setRelatorioDeDesempenho(relatorioDesempenho);
        }

        if (request.getRelatorioTaticoPsicologico() != null) {
            relatorioTaticoPsicologico relatorioTaticoPsicologico = new relatorioTaticoPsicologico();
            relatorioTaticoPsicologico.setEsportividade(request.getRelatorioTaticoPsicologico().getEsportividade());
            relatorioTaticoPsicologico.setDisciplina(request.getRelatorioTaticoPsicologico().getDisciplina());
            relatorioTaticoPsicologico.setFoco(request.getRelatorioTaticoPsicologico().getFoco());
            relatorioTaticoPsicologico.setConfianca(request.getRelatorioTaticoPsicologico().getConfianca());
            relatorioTaticoPsicologico.setTomadaDecisoes(request.getRelatorioTaticoPsicologico().getTomadaDecisoes());
            relatorioTaticoPsicologico.setCompromisso(request.getRelatorioTaticoPsicologico().getCompromisso());
            relatorioTaticoPsicologico.setLideranca(request.getRelatorioTaticoPsicologico().getLideranca());
            relatorioTaticoPsicologico.setTrabalhoEmEquipe(request.getRelatorioTaticoPsicologico().getTrabalhoEmEquipe());
            relatorioTaticoPsicologico.setAtributosFisicos(request.getRelatorioTaticoPsicologico().getAtributosFisicos());
            relatorioTaticoPsicologico.setAtuarSobPressao(request.getRelatorioTaticoPsicologico().getAtuarSobPressao());
            relatorioTaticoPsicologico.setAtleta(atleta);
            relatorioGeral.setRelatorioTaticoPsicologico(relatorioTaticoPsicologico);
        }

        relatorioAvaliacaoGeral savedRelatorio = relatorioAvaliacaoGeralRepository.save(relatorioGeral);

        // --- LÓGICA CORRIGIDA: Geração e salvamento da análise de IA ---
        List<relatorioAvaliacaoGeral> allEvaluations = relatorioAvaliacaoGeralRepository.findAllByAtletaIdOrderByDataAvaliacaoAsc(atleta.getId());

        List<geminiAnalysisService.EvaluationScores> evaluationScoresList = allEvaluations.stream()
                .map(com.br.Service.geminiAnalysisService.EvaluationScores::new)
                .collect(Collectors.toList());

        // Chamamos o serviço do Gemini. O salvamento agora ocorre DENTRO dele.
        geminiAnalysisService.generateComprehensiveAnalysis(
                atleta.getNome(),
                evaluationScoresList,
                atleta.getEmail()
        ).block(); // O .block() é necessário aqui para aguardar a conclusão da chamada assíncrona.

        // Retorna o DTO de resposta (lógica existente)
        avaliacaoGeralResponse response = new avaliacaoGeralResponse();
        response.setId(savedRelatorio.getId());
        response.setAtletaId(savedRelatorio.getAtleta().getId());
        response.setNomeAtleta(savedRelatorio.getAtleta().getNome());
        response.setUserName(savedRelatorio.getUserName());

        if (savedRelatorio.getPosicao() != null) {
            response.setPosicao(savedRelatorio.getPosicao().name());
        }
        if (savedRelatorio.getDataAvaliacao() != null) {
            response.setDataAvaliacao(savedRelatorio.getDataAvaliacao().format(DATE_FORMATTER));
        }
        response.setPeriodoTreino(savedRelatorio.getPeriodoTreino());

        if (savedRelatorio.getSubDivisao() != null) {
            response.setSubDivisao(savedRelatorio.getSubDivisao().name());
        }

        response.setFeedbackTreinador(savedRelatorio.getFeedbackTreinador());
        response.setFeedbackAvaliador(savedRelatorio.getFeedbackAvaliador());
        response.setPontosFortes(request.getPontosFortes());
        response.setPontosFracos(request.getPontosFracos());
        response.setAreasAprimoramento(request.getAreasAprimoramento());
        response.setMetasObjetivos(request.getMetasObjetivos());

        if (savedRelatorio.getRelatorioDesempenho() != null) {
            response.setRelatorioDesempenho(new relatorioDesempenhoResponse(savedRelatorio.getRelatorioDesempenho()));
        }
        if (savedRelatorio.getRelatorioTaticoPsicologico() != null) {
            response.setRelatorioTaticoPsicologico(new relatorioTaticoPsicologicoResponse(savedRelatorio.getRelatorioTaticoPsicologico()));
        }

        return response;
    }

    // ... (restante dos métodos do serviço, como findById, deleteById, listarRelatorioGeral e atualizarRelatorioGeral) ...
    @Transactional(readOnly = true)
    public Optional<relatorioAvaliacaoGeral> findById(UUID id) {
        return relatorioAvaliacaoGeralRepository.findById(id);
    }

    @Transactional
    public void deleteById(UUID id) {
        relatorioAvaliacaoGeralRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<avaliacaoGeralResponse> listarRelatorioGeral() {
        List<relatorioAvaliacaoGeral> avaliacoes = relatorioAvaliacaoGeralRepository.findAll();

        return avaliacoes.stream().map(avaliacao -> {
            avaliacaoGeralResponse dto = new avaliacaoGeralResponse();
            dto.setId(avaliacao.getId());
            if (avaliacao.getAtleta() != null) {
                dto.setAtletaId(avaliacao.getAtleta().getId());
                dto.setNomeAtleta(avaliacao.getAtleta().getNome());
            } else {
                dto.setAtletaId(null);
                dto.setNomeAtleta("Atleta Desconhecido");
            }
            dto.setUserName(avaliacao.getUserName());
            if (avaliacao.getDataAvaliacao() != null) {
                dto.setDataAvaliacao(avaliacao.getDataAvaliacao().format(DATE_FORMATTER));
            } else {
                dto.setDataAvaliacao(null);
            }
            dto.setPeriodoTreino(avaliacao.getPeriodoTreino());

            if (avaliacao.getSubDivisao() != null) {
                dto.setSubDivisao(avaliacao.getSubDivisao().name());
            } else {
                dto.setSubDivisao(null);
            }

            if (avaliacao.getPosicao() != null) {
                dto.setPosicao(avaliacao.getPosicao().name());
            } else {
                dto.setPosicao(null);
            }

            dto.setFeedbackTreinador(avaliacao.getFeedbackTreinador());
            dto.setFeedbackAvaliador(avaliacao.getFeedbackAvaliador());
            dto.setPontosFortes(avaliacao.getPontosFortes());
            dto.setPontosFracos(avaliacao.getPontosFracos());
            dto.setAreasAprimoramento(avaliacao.getAreasAprimoramento());
            dto.setMetasObjetivos(avaliacao.getMetasObjetivos());

            if (avaliacao.getRelatorioDesempenho() != null) {
                dto.setRelatorioDesempenho(new relatorioDesempenhoResponse(avaliacao.getRelatorioDesempenho()));
            }
            if (avaliacao.getRelatorioTaticoPsicologico() != null) {
                dto.setRelatorioTaticoPsicologico(new relatorioTaticoPsicologicoResponse(avaliacao.getRelatorioTaticoPsicologico()));
            }

            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public relatorioAvaliacaoGeral atualizarRelatorioGeral(UUID id, relatorioAvaliacaoGeral relatorioAvaliacaoGeralAtualizado) {
        Optional<relatorioAvaliacaoGeral> existingRelatorioOptional = relatorioAvaliacaoGeralRepository.findById(id);

        if (existingRelatorioOptional.isPresent()) {
            relatorioAvaliacaoGeral relatorioExistente = existingRelatorioOptional.get();

            relatorioExistente.setDataAvaliacao(relatorioAvaliacaoGeralAtualizado.getDataAvaliacao());
            relatorioExistente.setPeriodoTreino(relatorioAvaliacaoGeralAtualizado.getPeriodoTreino());
            relatorioExistente.setFeedbackTreinador(relatorioAvaliacaoGeralAtualizado.getFeedbackTreinador());
            relatorioExistente.setFeedbackAvaliador(relatorioAvaliacaoGeralAtualizado.getFeedbackAvaliador());
            relatorioExistente.setPontosFortes(relatorioAvaliacaoGeralAtualizado.getPontosFortes());
            relatorioExistente.setPontosFracos(relatorioAvaliacaoGeralAtualizado.getPontosFracos());
            relatorioExistente.setAreasAprimoramento(relatorioAvaliacaoGeralAtualizado.getAreasAprimoramento());
            relatorioExistente.setMetasObjetivos(relatorioAvaliacaoGeralAtualizado.getMetasObjetivos());

            if (relatorioAvaliacaoGeralAtualizado.getSubDivisao() != null) {
                relatorioExistente.setSubDivisao(relatorioAvaliacaoGeralAtualizado.getSubDivisao());
            } else {
                relatorioExistente.setSubDivisao(null);
            }

            if (relatorioAvaliacaoGeralAtualizado.getPosicao() != null) {
                relatorioExistente.setPosicao(relatorioAvaliacaoGeralAtualizado.getPosicao());
            } else {
                relatorioExistente.setPosicao(null);
            }

            if (relatorioAvaliacaoGeralAtualizado.getRelatorioDesempenho() != null) {
                relatorioDesempenho novoDesempenho = relatorioAvaliacaoGeralAtualizado.getRelatorioDesempenho();
                if (relatorioExistente.getRelatorioDesempenho() != null) {
                    relatorioDesempenho desempenhoExistente = relatorioExistente.getRelatorioDesempenho();
                    desempenhoExistente.setControle(novoDesempenho.getControle());
                    desempenhoExistente.setRecepcao(novoDesempenho.getRecepcao());
                    desempenhoExistente.setDribles(novoDesempenho.getDribles());
                    desempenhoExistente.setPasse(novoDesempenho.getPasse());
                    desempenhoExistente.setTiro(novoDesempenho.getTiro());
                    desempenhoExistente.setCruzamento(novoDesempenho.getCruzamento());
                    desempenhoExistente.setGiro(novoDesempenho.getGiro());
                    desempenhoExistente.setManuseioDeBola(novoDesempenho.getManuseioDeBola());
                    desempenhoExistente.setForcaChute(novoDesempenho.getForcaChute());
                    desempenhoExistente.setGerenciamentoDeGols(novoDesempenho.getGerenciamentoDeGols());
                    desempenhoExistente.setJogoOfensivo(novoDesempenho.getJogoOfensivo());
                    desempenhoExistente.setJogoDefensivo(novoDesempenho.getJogoDefensivo());
                    desempenhoExistente.setAtleta(relatorioExistente.getAtleta());
                } else {
                    relatorioExistente.setRelatorioDeDesempenho(novoDesempenho);
                    novoDesempenho.setAtleta(relatorioExistente.getAtleta());
                }
            }

            if (relatorioAvaliacaoGeralAtualizado.getRelatorioTaticoPsicologico() != null) {
                relatorioTaticoPsicologico novoTatico = relatorioAvaliacaoGeralAtualizado.getRelatorioTaticoPsicologico();
                if (relatorioExistente.getRelatorioTaticoPsicologico() != null) {
                    relatorioTaticoPsicologico taticoExistente = relatorioExistente.getRelatorioTaticoPsicologico();
                    taticoExistente.setEsportividade(novoTatico.getEsportividade());
                    taticoExistente.setDisciplina(novoTatico.getDisciplina());
                    taticoExistente.setFoco(novoTatico.getFoco());
                    taticoExistente.setConfianca(novoTatico.getConfianca());
                    taticoExistente.setTomadaDecisoes(novoTatico.getTomadaDecisoes());
                    taticoExistente.setCompromisso(novoTatico.getCompromisso());
                    taticoExistente.setLideranca(novoTatico.getLideranca());
                    taticoExistente.setTrabalhoEmEquipe(novoTatico.getTrabalhoEmEquipe());
                    taticoExistente.setAtributosFisicos(novoTatico.getAtributosFisicos());
                    taticoExistente.setAtuarSobPressao(novoTatico.getAtuarSobPressao());
                    taticoExistente.setAtleta(relatorioExistente.getAtleta());
                } else {
                    relatorioExistente.setRelatorioTaticoPsicologico(novoTatico);
                    novoTatico.setAtleta(relatorioExistente.getAtleta());
                }
            }

            // Salva o relatório atualizado
            relatorioAvaliacaoGeralRepository.save(relatorioExistente);

            // --- LÓGICA CORRIGIDA: Geração e salvamento da análise de IA após a atualização ---
            List<relatorioAvaliacaoGeral> allEvaluations = relatorioAvaliacaoGeralRepository.findAllByAtletaId(relatorioExistente.getAtleta().getId());

            List<geminiAnalysisService.EvaluationScores> evaluationScoresList = allEvaluations.stream()
                    .map(com.br.Service.geminiAnalysisService.EvaluationScores::new)
                    .collect(Collectors.toList());

            // Chamamos o serviço do Gemini. O salvamento agora ocorre DENTRO dele.
            geminiAnalysisService.generateComprehensiveAnalysis(
                    relatorioExistente.getAtleta().getNome(),
                    evaluationScoresList,
                    relatorioExistente.getAtleta().getEmail()
            ).block(); // O .block() é necessário aqui para aguardar a conclusão da chamada assíncrona.

            return relatorioExistente;
        } else {
            throw new RuntimeException("Relatório de Avaliação Geral com ID " + id + " não encontrado.");
        }
    }
}