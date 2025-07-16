package com.br.Service;

import com.br.Entity.*;
import com.br.Repository.AtletaRepository;
import com.br.Repository.RelatorioAvaliacaoGeralRepository;
import com.br.Request.CriarAvaliacaoRequest; // Importar o DTO de Requisição
import com.br.Response.AvaliacaoGeralResponse; // Importar o DTO de Resposta
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RelatorioAvaliacaoGService {

    private final RelatorioAvaliacaoGeralRepository relatorioAvaliacaoGeralRepository;
    private final AtletaRepository atletaRepository;

    @Autowired
    public RelatorioAvaliacaoGService(RelatorioAvaliacaoGeralRepository relatorioAvaliacaoGeralRepository,
                                      AtletaRepository atletaRepository) {
        this.relatorioAvaliacaoGeralRepository = relatorioAvaliacaoGeralRepository;
        this.atletaRepository = atletaRepository;
    }

    /**
     * Cadastra um novo Relatório de Avaliação Geral completo, incluindo dados de desempenho e tático/psicológico.
     * Recebe um CriarAvaliacaoRequest, mapeia para as entidades e persiste.
     * Retorna um AvaliacaoGeralResponse com informações resumidas.
     *
     * @param request O DTO de requisição contendo todos os dados da avaliação.
     * @return Um AvaliacaoGeralResponse com informações do relatório criado.
     * @throws IllegalArgumentException se o ID do Atleta for nulo.
     * @throws RuntimeException se o Atleta não for encontrado.
     */
    @Transactional
    public AvaliacaoGeralResponse cadastrarRelatorioGeral(CriarAvaliacaoRequest request) {
        // 1. Buscar e validar o Atleta
        if (request.getAtletaId() == null) {
            throw new IllegalArgumentException("ID do Atleta é obrigatório para cadastrar o relatório geral.");
        }
        Atleta atleta = atletaRepository.findById(request.getAtletaId())
                .orElseThrow(() -> new RuntimeException("Atleta com ID " + request.getAtletaId() + " não encontrado."));

        // 2. Criar a entidade RelatorioAvaliacaoGeral e popular seus campos
        RelatorioAvaliacaoGeral relatorioGeral = new RelatorioAvaliacaoGeral();
        relatorioGeral.setAtleta(atleta);
        relatorioGeral.setUserName(request.getUserName());
        relatorioGeral.setSubDivisao(atleta.getSubDivisao());
        relatorioGeral.setDataAvaliacao(request.getDataAvaliacao());
        relatorioGeral.setPeriodoTreino(request.getPeriodoTreino());
        relatorioGeral.setFeedbackTreinador(request.getFeedbackTreinador());
        relatorioGeral.setFeedbackAvaliador(request.getFeedbackAvaliador());
        relatorioGeral.setPontosFortes(request.getPontosFortes());
        relatorioGeral.setPontosFracos(request.getPontosFracos());
        relatorioGeral.setAreasAprimoramento(request.getAreasAprimoramento());
        relatorioGeral.setMetasObjetivos(request.getMetasObjetivos());

        // 3. Criar e associar a entidade RelatorioDesempenho
        if (request.getRelatorioDesempenho() != null) {
            RelatorioDesempenho relatorioDesempenho = new RelatorioDesempenho();
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
            relatorioDesempenho.setAtleta(atleta); // Associar o atleta também ao relatório filho
            relatorioGeral.setRelatorioDeDesempenho(relatorioDesempenho); // Seta a bidirecionalidade
        }

        // 4. Criar e associar a entidade RelatorioTaticoPsicologico
        if (request.getRelatorioTaticoPsicologico() != null) {
            RelatorioTaticoPsicologico relatorioTaticoPsicologico = new RelatorioTaticoPsicologico();
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
            relatorioTaticoPsicologico.setAtleta(atleta); // Associar o atleta também ao relatório filho
            relatorioGeral.setRelatorioTaticoPsicologico(relatorioTaticoPsicologico); // Seta a bidirecionalidade
        }

        // 5. Salvar o relatório geral (que irá cascatear o salvamento dos filhos)
        RelatorioAvaliacaoGeral savedRelatorio = relatorioAvaliacaoGeralRepository.save(relatorioGeral);

        // 6. Criar e retornar o DTO de resposta
        return new AvaliacaoGeralResponse();
    }

    /**
     * Busca um Relatório de Avaliação Geral pelo seu ID.
     *
     * @param id O ID do relatório a ser buscado.
     * @return Um Optional contendo o Relatório de Avaliação Geral, se encontrado.
     */
    @Transactional(readOnly = true) // Adicionado readOnly para otimização em operações de leitura
    public Optional<RelatorioAvaliacaoGeral> findById(Long id) {
        return relatorioAvaliacaoGeralRepository.findById(id);
    }

    @Transactional
    public void deleteById(Long id) {
        relatorioAvaliacaoGeralRepository.deleteById(id);
    }

    @Transactional(readOnly = true) // Adicionado readOnly para otimização em operações de leitura
    public List<RelatorioAvaliacaoGeral> listarRelatorioGeral() {
        return relatorioAvaliacaoGeralRepository.findAll();
    }

    @Transactional
    public RelatorioAvaliacaoGeral atualizarRelatorioGeral(Long id, RelatorioAvaliacaoGeral relatorioAvaliacaoGeralAtualizado) {
        Optional<RelatorioAvaliacaoGeral> existingRelatorioOptional = relatorioAvaliacaoGeralRepository.findById(id);

        if (existingRelatorioOptional.isPresent()) {
            RelatorioAvaliacaoGeral relatorioExistente = existingRelatorioOptional.get();

            // Atualiza os campos do relatório geral com os novos valores
            relatorioExistente.setDataAvaliacao(relatorioAvaliacaoGeralAtualizado.getDataAvaliacao());
            relatorioExistente.setPeriodoTreino(relatorioAvaliacaoGeralAtualizado.getPeriodoTreino());
            relatorioExistente.setFeedbackTreinador(relatorioAvaliacaoGeralAtualizado.getFeedbackTreinador());
            relatorioExistente.setFeedbackAvaliador(relatorioAvaliacaoGeralAtualizado.getFeedbackAvaliador());
            relatorioExistente.setPontosFortes(relatorioAvaliacaoGeralAtualizado.getPontosFortes());
            relatorioExistente.setPontosFracos(relatorioAvaliacaoGeralAtualizado.getPontosFracos());
            relatorioExistente.setAreasAprimoramento(relatorioAvaliacaoGeralAtualizado.getAreasAprimoramento());
            relatorioExistente.setMetasObjetivos(relatorioAvaliacaoGeralAtualizado.getMetasObjetivos());

            // A associação com Atleta geralmente não muda em uma atualização.
            // Se o Atleta puder ser alterado, a lógica para buscar e setar o novo atleta seria adicionada aqui.

            // Atualiza os relatórios filhos (Desempenho e Tático/Psicológico)
            // A lógica aqui é para atualizar os filhos existentes ou criar novos se não existirem
            if (relatorioAvaliacaoGeralAtualizado.getRelatorioDesempenho() != null) {
                RelatorioDesempenho novoDesempenho = relatorioAvaliacaoGeralAtualizado.getRelatorioDesempenho();
                if (relatorioExistente.getRelatorioDesempenho() != null) {
                    // Se já existe um relatório de desempenho, atualiza os campos
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
                    // Garante que o atleta também esteja setado no filho se for uma nova criação ou atualização
                    desempenhoExistente.setAtleta(relatorioExistente.getAtleta());
                } else {
                    // Se não existe, cria um novo e associa
                    relatorioExistente.setRelatorioDeDesempenho(novoDesempenho);
                    novoDesempenho.setAtleta(relatorioExistente.getAtleta()); // Associar ao atleta
                }
            }
            // Repetir a lógica para RelatorioTaticoPsicologico
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
                    // Garante que o atleta também esteja setado no filho
                    taticoExistente.setAtleta(relatorioExistente.getAtleta());
                } else {
                    relatorioExistente.setRelatorioTaticoPsicologico(novoTatico);
                    novoTatico.setAtleta(relatorioExistente.getAtleta()); // Associar ao atleta
                }
            }

            return relatorioAvaliacaoGeralRepository.save(relatorioExistente);
        } else {
            throw new RuntimeException("Relatório de Avaliação Geral com ID " + id + " não encontrado.");
        }
    }
}
