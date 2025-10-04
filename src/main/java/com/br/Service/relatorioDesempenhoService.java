package com.br.Service;

import com.br.Entity.atleta; // Importar a entidade Atleta
import com.br.Entity.relatorioDesempenho;
import com.br.Entity.relatorioAvaliacaoGeral; // Importar RelatorioAvaliacaoGeral, se necessário para buscar
import com.br.Repository.atletaRepository; // Importar o repositório de Atleta
import com.br.Repository.relatorioDesempenhoRepository;
import com.br.Repository.relatorioAvaliacaoGeralRepository; // Importar RelatorioAvaliacaoGeralRepository, se necessário

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class relatorioDesempenhoService {

    private final relatorioDesempenhoRepository relatorioDesempenhoRepository;
    private final atletaRepository atletaRepository; // Injetar AtletaRepository
    private final relatorioAvaliacaoGeralRepository relatorioAvaliacaoGeralRepository; // Injetar se precisar buscar o pai

    @Autowired
    public relatorioDesempenhoService(relatorioDesempenhoRepository relatorioDesempenhoRepository,
                                      atletaRepository atletaRepository,
                                      relatorioAvaliacaoGeralRepository relatorioAvaliacaoGeralRepository) {
        this.relatorioDesempenhoRepository = relatorioDesempenhoRepository;
        this.atletaRepository = atletaRepository; // Inicializar AtletaRepository
        this.relatorioAvaliacaoGeralRepository = relatorioAvaliacaoGeralRepository; // Inicializar
    }

    @Transactional
    public relatorioDesempenho cadastrarRelatorioDesempenho(relatorioDesempenho relatorioDesempenho) {
        // Verifica se o atleta está presente e o associa
        if (relatorioDesempenho.getAtleta() == null || relatorioDesempenho.getAtleta().getId() == null) {
            throw new IllegalArgumentException("ID do Atleta é obrigatório para cadastrar o relatório de desempenho.");
        }
        atleta atleta = atletaRepository.findById(relatorioDesempenho.getAtleta().getId())
                .orElseThrow(() -> new RuntimeException("Atleta com ID " + relatorioDesempenho.getAtleta().getId() + " não encontrado."));
        relatorioDesempenho.setAtleta(atleta);

        if (relatorioDesempenho.getRelatorioAvaliacaoGeral() != null && relatorioDesempenho.getRelatorioAvaliacaoGeral().getId() != null) {
            relatorioAvaliacaoGeral relatorioGeralPai = relatorioAvaliacaoGeralRepository.findById(relatorioDesempenho.getRelatorioAvaliacaoGeral().getId())
                    .orElseThrow(() -> new RuntimeException("Relatório de Avaliação Geral pai com ID " + relatorioDesempenho.getRelatorioAvaliacaoGeral().getId() + " não encontrado."));
            relatorioDesempenho.setRelatorioAvaliacaoGeral(relatorioGeralPai);
        }

        return relatorioDesempenhoRepository.save(relatorioDesempenho);
    }

    @Transactional
    public void deleteByIdDesempenho(UUID id) {
        relatorioDesempenhoRepository.deleteById(id);
    }

    @Transactional
    public List<relatorioDesempenho> listarRelatorioDesempenho() {
        return relatorioDesempenhoRepository.findAll();
    }

    @Transactional
    public relatorioDesempenho atualizarRelatorioDesempenho(UUID id, relatorioDesempenho relatorioDesempenhoAtualizado) {
        Optional<relatorioDesempenho> existingRelatorioOptional = relatorioDesempenhoRepository.findById(id);

        if (existingRelatorioOptional.isPresent()) {
            relatorioDesempenho relatorioExistente = existingRelatorioOptional.get();

            // Atualiza os campos do relatório de desempenho com os novos valores
            relatorioExistente.setControle(relatorioDesempenhoAtualizado.getControle());
            relatorioExistente.setRecepcao(relatorioDesempenhoAtualizado.getRecepcao());
            relatorioExistente.setDribles(relatorioDesempenhoAtualizado.getDribles());
            relatorioExistente.setPasse(relatorioDesempenhoAtualizado.getPasse());
            relatorioExistente.setTiro(relatorioDesempenhoAtualizado.getTiro());
            relatorioExistente.setCruzamento(relatorioDesempenhoAtualizado.getCruzamento());
            relatorioExistente.setGiro(relatorioDesempenhoAtualizado.getGiro());
            relatorioExistente.setManuseioDeBola(relatorioDesempenhoAtualizado.getManuseioDeBola());
            relatorioExistente.setForcaChute(relatorioDesempenhoAtualizado.getForcaChute());
            relatorioExistente.setGerenciamentoDeGols(relatorioDesempenhoAtualizado.getGerenciamentoDeGols());
            relatorioExistente.setJogoOfensivo(relatorioDesempenhoAtualizado.getJogoOfensivo());
            relatorioExistente.setJogoDefensivo(relatorioDesempenhoAtualizado.getJogoDefensivo());

            return relatorioDesempenhoRepository.save(relatorioExistente);
        } else {
            throw new RuntimeException("Relatório de Desempenho com ID " + id + " não encontrado.");
        }
    }
}
