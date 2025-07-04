package com.br.Service;

import com.br.Entity.Atleta;
import com.br.Entity.Presenca;
import com.br.Repository.AtletaRepository;
import com.br.Repository.PresencaRepository;
import com.br.Request.PresencaRequest;
import com.br.Response.HistoricoPresencaResponse;
import com.br.Response.PresencaResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate; // Importe LocalDate
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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


   public List<PresencaResponse> getLista() {
        List <Presenca> lista = presencaRepository.findAll();

        return lista.stream().map(presenca -> new PresencaResponse(presenca.getId(),
                presenca.getAtleta().getNome(),
                presenca.getPresente()))
                .collect(Collectors.toList());
   }
    public List<HistoricoPresencaResponse> getHistoricoPresencas() {
        // Busca todas as presenças
        List<Presenca> todasPresencas = presencaRepository.findAll();

        // Mapeia para o DTO de resposta do histórico
        return todasPresencas.stream()
                .map(presenca -> new HistoricoPresencaResponse(
                        presenca.getId(),
                        presenca.getData(),
                        presenca.getPresente(),
                        presenca.getAtleta().getId(),
                        presenca.getAtleta().getNome() // Assumindo que Atleta tem um método getNome()
                ))
                .collect(Collectors.toList());
    }
    public List<PresencaResponse> getAtletasComPresencaNaData(LocalDate data) {
        // Busca todos os atletas cadastrados
        List<Atleta> todosAtletas = atletaRepository.findAll();

        // Busca presenças registradas na data específica
        List<Presenca> presencasNaData = presencaRepository.findByData(data);

        return todosAtletas.stream().map(atleta -> {

            Optional<Presenca> presenca = presencasNaData.stream()
                    .filter(p -> p.getAtleta().getId().equals(atleta.getId()))
                    .findFirst();

            return new PresencaResponse(
                    atleta.getId(),
                    atleta.getNome(),
                    presenca.isPresent() ? presenca.get().getPresente() : null
            );
        }).collect(Collectors.toList());
    }



}