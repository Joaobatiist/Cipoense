package com.br.Service;

import com.br.Entity.Atleta;
import com.br.Entity.Eventos;
import com.br.Entity.Presenca;
import com.br.Repository.AtletaRepository;
import com.br.Repository.PresencaRepository;
import com.br.Request.PresencaRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate; // Importe LocalDate
import java.util.List;
import java.util.Optional;

@Service
public class PresencaService {

    private final PresencaRepository presencaRepository;
    private final AtletaRepository atletaRepository;

    public PresencaService(PresencaRepository presencaRepository, AtletaRepository atletaRepository) {
        this.presencaRepository = presencaRepository;
        this.atletaRepository = atletaRepository;
    }

    @Transactional
    public void registrarPresencas(List<PresencaRequest> presencas) {
        for (PresencaRequest dto : presencas) {
            // 1. Buscar o atleta pelo ID. Se não encontrar, lança uma exceção.
            Atleta atleta = atletaRepository.findById(dto.getAtletaId())
                    .orElseThrow(() -> new RuntimeException("Atleta não encontrado com ID: " + dto.getAtletaId()));

            // 2. Converter a string de data para LocalDate (se dto.getData() for String)
            // Assumindo que dto.getData() é uma String no formato "YYYY-MM-DD"
            LocalDate dataPresenca = dto.getData();

            // 3. Tenta encontrar uma presença existente para o atleta NA DATA ESPECÍFICA
            Optional<Presenca> presencaExistente = presencaRepository.findByAtletaAndData(atleta, dataPresenca); // Use LocalDate aqui

            if (presencaExistente.isPresent()) {
                // Se já existe, ATUALIZA o registro existente
                Presenca presenca = presencaExistente.get();
                presenca.setPresente(dto.getPresente()); // Atualiza apenas o status de presença
                presencaRepository.save(presenca);
            } else {
                // Se não existe, CRIA um novo registro de presença
                Presenca novaPresenca = new Presenca();
                novaPresenca.setAtleta(atleta); // **Essencial: Defina o objeto Atleta**
                novaPresenca.setPresente(dto.getPresente()); // **Essencial: Defina o status de presença**
                novaPresenca.setData(dataPresenca); // **Essencial: Defina a data da presença**
                presencaRepository.save(novaPresenca);
            }
        }
    }


}