package com.br.Service;

import com.br.Entity.*;
import com.br.Repository.AtletaRepository;
import com.br.Repository.RelatorioAvaliacaoGeralRepository;
import com.br.Request.CriarAvaliacaoRequest;
import com.br.Response.AvaliacaoGeralResponse;
import com.br.Response.RelatorioDesempenhoResponse;
import com.br.Response.RelatorioTaticoPsicologicoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RelatorioAvaliacaoGService {

    private final RelatorioAvaliacaoGeralRepository relatorioAvaliacaoGeralRepository;
    private final AtletaRepository atletaRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Autowired
    public RelatorioAvaliacaoGService(RelatorioAvaliacaoGeralRepository relatorioAvaliacaoGeralRepository,
                                      AtletaRepository atletaRepository) {
        this.relatorioAvaliacaoGeralRepository = relatorioAvaliacaoGeralRepository;
        this.atletaRepository = atletaRepository;
    }

    @Transactional
    public AvaliacaoGeralResponse cadastrarRelatorioGeral(CriarAvaliacaoRequest request) {
        if (request.getAtletaId() == null) {
            throw new IllegalArgumentException("ID do Atleta é obrigatório para cadastrar o relatório geral.");
        }
        Atleta atleta = atletaRepository.findById(request.getAtletaId())
                .orElseThrow(() -> new RuntimeException("Atleta com ID " + request.getAtletaId() + " não encontrado."));

        RelatorioAvaliacaoGeral relatorioGeral = new RelatorioAvaliacaoGeral();
        relatorioGeral.setAtleta(atleta);
        relatorioGeral.setUserName(request.getUserName());


        if (atleta.getSubDivisao() != null) {
            relatorioGeral.setSubDivisao(atleta.getSubDivisao()); // Convertendo ENUM para String
        } else {
            relatorioGeral.setSubDivisao(null);
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
            RelatorioDesempenho relatorioDesempenho = new RelatorioDesempenho();
            // ... (população dos campos de RelatorioDesempenho)
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
            RelatorioTaticoPsicologico relatorioTaticoPsicologico = new RelatorioTaticoPsicologico();
            // ... (população dos campos de RelatorioTaticoPsicologico)
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

        RelatorioAvaliacaoGeral savedRelatorio = relatorioAvaliacaoGeralRepository.save(relatorioGeral);

        AvaliacaoGeralResponse response = new AvaliacaoGeralResponse();
        response.setId(savedRelatorio.getId());
        response.setAtletaId(savedRelatorio.getAtleta().getId());
        response.setNomeAtleta(savedRelatorio.getAtleta().getNome());
        response.setUserName(savedRelatorio.getUserName());
        if (savedRelatorio.getDataAvaliacao() != null) {
            response.setDataAvaliacao(savedRelatorio.getDataAvaliacao().format(DATE_FORMATTER));
        } else {
            response.setDataAvaliacao(null);
        }
        response.setPeriodoTreino(savedRelatorio.getPeriodoTreino());

        if (savedRelatorio.getSubDivisao() != null) {
            // Se savedRelatorio.getSubDivisao() for um enum, use .name()
            if (savedRelatorio.getSubDivisao() instanceof Enum) { // Verificação para garantir que é um enum
                response.setSubDivisao(savedRelatorio.getSubDivisao().name());
            } else { // Se já for String (o que seria ideal no DTO)
                response.setSubDivisao(savedRelatorio.getSubDivisao().toString()); // Ou apenas getSubDivisao() se já for String
            }
        } else {
            response.setSubDivisao(null);
        }

        response.setFeedbackTreinador(savedRelatorio.getFeedbackTreinador());
        response.setFeedbackAvaliador(savedRelatorio.getFeedbackAvaliador());
        response.setPontosFortes(savedRelatorio.getPontosFortes());
        response.setPontosFracos(savedRelatorio.getPontosFracos());
        response.setAreasAprimoramento(savedRelatorio.getAreasAprimoramento());
        response.setMetasObjetivos(savedRelatorio.getMetasObjetivos());

        if (savedRelatorio.getRelatorioDesempenho() != null) {
            response.setRelatorioDesempenho(new RelatorioDesempenhoResponse(savedRelatorio.getRelatorioDesempenho()));
        }
        if (savedRelatorio.getRelatorioTaticoPsicologico() != null) {
            response.setRelatorioTaticoPsicologico(new RelatorioTaticoPsicologicoResponse(savedRelatorio.getRelatorioTaticoPsicologico()));
        }
        return response;
    }

    @Transactional(readOnly = true)
    public Optional<RelatorioAvaliacaoGeral> findById(Long id) {
        return relatorioAvaliacaoGeralRepository.findById(id);
    }

    @Transactional
    public void deleteById(Long id) {
        relatorioAvaliacaoGeralRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoGeralResponse> listarRelatorioGeral() {
        List<RelatorioAvaliacaoGeral> avaliacoes = relatorioAvaliacaoGeralRepository.findAll();

        return avaliacoes.stream().map(avaliacao -> {
            AvaliacaoGeralResponse dto = new AvaliacaoGeralResponse();
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

            // **CORREÇÃO AQUI (se RelatorioAvaliacaoGeral.subDivisao é SubDivisao (enum) e DTO espera String):**
            // Se `avaliacao.getSubDivisao()` está retornando o ENUM, precisamos do `.name()`.
            if (avaliacao.getSubDivisao() != null) {
                // Verificação para garantir que é um enum antes de chamar .name()
                if (avaliacao.getSubDivisao() instanceof Enum) {
                    dto.setSubDivisao(avaliacao.getSubDivisao().name());
                } else {
                    dto.setSubDivisao(avaliacao.getSubDivisao().toString()); // Caso seja um objeto SubDivisao complexo, ou já String
                }
            } else {
                dto.setSubDivisao(null);
            }

            dto.setFeedbackTreinador(avaliacao.getFeedbackTreinador());
            dto.setFeedbackAvaliador(avaliacao.getFeedbackAvaliador());
            dto.setPontosFortes(avaliacao.getPontosFortes());
            dto.setPontosFracos(avaliacao.getPontosFracos());
            dto.setAreasAprimoramento(avaliacao.getAreasAprimoramento());
            dto.setMetasObjetivos(avaliacao.getMetasObjetivos());

            if (avaliacao.getRelatorioDesempenho() != null) {
                dto.setRelatorioDesempenho(new RelatorioDesempenhoResponse(avaliacao.getRelatorioDesempenho()));
            }
            if (avaliacao.getRelatorioTaticoPsicologico() != null) {
                dto.setRelatorioTaticoPsicologico(new RelatorioTaticoPsicologicoResponse(avaliacao.getRelatorioTaticoPsicologico()));
            }

            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public RelatorioAvaliacaoGeral atualizarRelatorioGeral(Long id, RelatorioAvaliacaoGeral relatorioAvaliacaoGeralAtualizado) {
        Optional<RelatorioAvaliacaoGeral> existingRelatorioOptional = relatorioAvaliacaoGeralRepository.findById(id);

        if (existingRelatorioOptional.isPresent()) {
            RelatorioAvaliacaoGeral relatorioExistente = existingRelatorioOptional.get();

            relatorioExistente.setDataAvaliacao(relatorioAvaliacaoGeralAtualizado.getDataAvaliacao());
            relatorioExistente.setPeriodoTreino(relatorioAvaliacaoGeralAtualizado.getPeriodoTreino());
            relatorioExistente.setFeedbackTreinador(relatorioAvaliacaoGeralAtualizado.getFeedbackTreinador());
            relatorioExistente.setFeedbackAvaliador(relatorioAvaliacaoGeralAtualizado.getFeedbackAvaliador());
            relatorioExistente.setPontosFortes(relatorioAvaliacaoGeralAtualizado.getPontosFortes());
            relatorioExistente.setPontosFracos(relatorioAvaliacaoGeralAtualizado.getPontosFracos());
            relatorioExistente.setAreasAprimoramento(relatorioAvaliacaoGeralAtualizado.getAreasAprimoramento());
            relatorioExistente.setMetasObjetivos(relatorioAvaliacaoGeralAtualizado.getMetasObjetivos());

            // **ATENÇÃO AQUI na atualização:**
            // Se `relatorioAvaliacaoGeralAtualizado.getSubDivisao()` é uma String
            // e `relatorioExistente.setSubDivisao()` espera um Enum, você precisaria fazer:
            // relatorioExistente.setSubDivisao(SubDivisao.valueOf(relatorioAvaliacaoGeralAtualizado.getSubDivisao()));
            // Mas se ambos são String (como o DTO de request e o campo da entidade RelatorioAvaliacaoGeral),
            // a atribuição direta é correta.
            // Pelo erro, a *entidade RelatorioAvaliacaoGeral* parece ter `SubDivisao` como um enum.
            // Se o request de PUT envia uma String, você precisaria converter:
            if (relatorioAvaliacaoGeralAtualizado.getSubDivisao() != null) {
                // Se o campo SubDivisao da *entidade* RelatorioAvaliacaoGeral é um ENUM
                // e o DTO de atualização (`relatorioAvaliacaoGeralAtualizado`) tem uma String,
                // você precisaria converter a String para o ENUM:
                // relatorioExistente.setSubDivisao(SubDivisao.valueOf(relatorioAvaliacaoGeralAtualizado.getSubDivisao()));

                // Se o campo SubDivisao da *entidade* RelatorioAvaliacaoGeral é uma String
                // e o DTO de atualização (`relatorioAvaliacaoGeralAtualizado`) tem uma String,
                // então a linha abaixo está correta:
                relatorioExistente.setSubDivisao(relatorioAvaliacaoGeralAtualizado.getSubDivisao());
            }

            if (relatorioAvaliacaoGeralAtualizado.getRelatorioDesempenho() != null) {
                RelatorioDesempenho novoDesempenho = relatorioAvaliacaoGeralAtualizado.getRelatorioDesempenho();
                if (relatorioExistente.getRelatorioDesempenho() != null) {
                    RelatorioDesempenho desempenhoExistente = relatorioExistente.getRelatorioDesempenho();
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
                RelatorioTaticoPsicologico novoTatico = relatorioAvaliacaoGeralAtualizado.getRelatorioTaticoPsicologico();
                if (relatorioExistente.getRelatorioTaticoPsicologico() != null) {
                    RelatorioTaticoPsicologico taticoExistente = relatorioExistente.getRelatorioTaticoPsicologico();
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

            return relatorioAvaliacaoGeralRepository.save(relatorioExistente);
        } else {
            throw new RuntimeException("Relatório de Avaliação Geral com ID " + id + " não encontrado.");
        }
    }
}