package com.br.Service;

import com.br.Entity.atleta; // Importar a entidade Atleta
import com.br.Entity.relatorioTaticoPsicologico;
import com.br.Entity.relatorioAvaliacaoGeral; // Importar RelatorioAvaliacaoGeral
import com.br.Repository.atletaRepository; // Importar o repositório de Atleta
import com.br.Repository.relatorioTaticoRepository;
import com.br.Repository.relatorioAvaliacaoGeralRepository; // Importar RelatorioAvaliacaoGeralRepository

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class relatorioTaticoService {

    private final relatorioTaticoRepository relatorioTaticoRepository;
    private final atletaRepository atletaRepository; // Injetar AtletaRepository
    private final relatorioAvaliacaoGeralRepository relatorioAvaliacaoGeralRepository; // Injetar se precisar buscar o pai

    @Autowired
    public relatorioTaticoService(relatorioTaticoRepository relatorioTaticoRepository,
                                  atletaRepository atletaRepository,
                                  relatorioAvaliacaoGeralRepository relatorioAvaliacaoGeralRepository) {
        this.relatorioTaticoRepository = relatorioTaticoRepository;
        this.atletaRepository = atletaRepository; // Inicializar AtletaRepository
        this.relatorioAvaliacaoGeralRepository = relatorioAvaliacaoGeralRepository; // Inicializar
    }

    @Transactional
    public relatorioTaticoPsicologico cadastrarRelatorioTatico(relatorioTaticoPsicologico relatorioTatico) {
        // Verifica se o atleta está presente e o associa
        if (relatorioTatico.getAtleta() == null || relatorioTatico.getAtleta().getId() == null) {
            throw new IllegalArgumentException("ID do Atleta é obrigatório para cadastrar o relatório tático/psicológico.");
        }
        atleta atleta = atletaRepository.findById(relatorioTatico.getAtleta().getId())
                .orElseThrow(() -> new RuntimeException("Atleta com ID " + relatorioTatico.getAtleta().getId() + " não encontrado."));
        relatorioTatico.setAtleta(atleta);

        if (relatorioTatico.getRelatorioAvaliacaoGeral() != null && relatorioTatico.getRelatorioAvaliacaoGeral().getId() != null) {
            relatorioAvaliacaoGeral relatorioGeralPai = relatorioAvaliacaoGeralRepository.findById(relatorioTatico.getRelatorioAvaliacaoGeral().getId())
                    .orElseThrow(() -> new RuntimeException("Relatório de Avaliação Geral pai com ID " + relatorioTatico.getRelatorioAvaliacaoGeral().getId() + " não encontrado."));
            relatorioTatico.setRelatorioAvaliacaoGeral(relatorioGeralPai);
        }

        return relatorioTaticoRepository.save(relatorioTatico);
    }


    @Transactional
    public void deleteByIdTatico(UUID id) { // Renomeado de deleteByIdTatico para deleteById
        relatorioTaticoRepository.deleteById(id);
    }

    @Transactional
    public List<relatorioTaticoPsicologico> listarRelatorioTatico() {
        return relatorioTaticoRepository.findAll();
    }

    @Transactional
    public relatorioTaticoPsicologico atualizarRelatorioTatico(UUID id, relatorioTaticoPsicologico relatorioTaticoPsicologicoAtualizado) { // Renomeado o método
        Optional<relatorioTaticoPsicologico> existingRelatorioOptional = relatorioTaticoRepository.findById(id); // Usa o ID do parâmetro

        if (existingRelatorioOptional.isPresent()) {
            relatorioTaticoPsicologico relatorioExistente = existingRelatorioOptional.get();

            // Atualiza os campos do relatório tático/psicológico com os novos valores
            relatorioExistente.setCompromisso(relatorioTaticoPsicologicoAtualizado.getCompromisso());
            relatorioExistente.setDisciplina(relatorioTaticoPsicologicoAtualizado.getDisciplina());
            relatorioExistente.setConfianca(relatorioTaticoPsicologicoAtualizado.getConfianca());
            relatorioExistente.setFoco(relatorioTaticoPsicologicoAtualizado.getFoco());
            relatorioExistente.setAtributosFisicos(relatorioTaticoPsicologicoAtualizado.getAtributosFisicos());
            relatorioExistente.setAtuarSobPressao(relatorioTaticoPsicologicoAtualizado.getAtuarSobPressao());
            relatorioExistente.setEsportividade(relatorioTaticoPsicologicoAtualizado.getEsportividade());
            relatorioExistente.setLideranca(relatorioTaticoPsicologicoAtualizado.getLideranca());
            relatorioExistente.setTomadaDecisoes(relatorioTaticoPsicologicoAtualizado.getTomadaDecisoes());
            relatorioExistente.setTrabalhoEmEquipe(relatorioTaticoPsicologicoAtualizado.getTrabalhoEmEquipe());

            return relatorioTaticoRepository.save(relatorioExistente);
        } else {
            throw new RuntimeException("Relatório Tático/Psicológico com ID " + id + " não encontrado.");
        }
    }
}
