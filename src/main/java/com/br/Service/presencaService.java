package com.br.Service;

import com.br.Entity.atleta;
import com.br.Entity.presenca;
import com.br.Repository.atletaRepository;
import com.br.Repository.presencaRepository;
import com.br.Request.presencaRequest;
import com.br.Response.historicoPresencaResponse;
import com.br.Response.presencaResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate; // Importe LocalDate
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class presencaService {

    private final presencaRepository presencaRepository;
    private final atletaRepository atletaRepository;

    public presencaService(presencaRepository presencaRepository, atletaRepository atletaRepository) {
        this.presencaRepository = presencaRepository;
        this.atletaRepository = atletaRepository;
    }

    @Transactional
    public void registrarPresencas(List<presencaRequest> presencas) {
        for (presencaRequest dto : presencas) {
            // 1. Buscar o atleta pelo ID. Se não encontrar, lança uma exceção.
            atleta atleta = atletaRepository.findById(dto.getAtletaId())
                    .orElseThrow(() -> new RuntimeException("Atleta não encontrado com ID: " + dto.getAtletaId()));


            LocalDate dataPresenca = dto.getData();

            // 3. Tenta encontrar uma presença existente para o atleta NA DATA ESPECÍFICA
            Optional<presenca> presencaExistente = presencaRepository.findByAtletaAndData(atleta, dataPresenca); // Use LocalDate aqui

            if (presencaExistente.isPresent()) {
                // Se já existe, ATUALIZA o registro existente
                presenca presenca = presencaExistente.get();
                presenca.setPresente(dto.getPresente()); // Atualiza apenas o status de presença
                presencaRepository.save(presenca);
            } else {
                // Se não existe, CRIA um novo registro de presença
                presenca novaPresenca = new presenca();
                novaPresenca.setAtleta(atleta); // **Essencial: Defina o objeto Atleta**
                novaPresenca.setPresente(dto.getPresente()); // **Essencial: Defina o status de presença**
                novaPresenca.setData(dataPresenca); // **Essencial: Defina a data da presença**
                presencaRepository.save(novaPresenca);
            }
        }
    }


   public List<presencaResponse> getLista() {
        List <presenca> lista = presencaRepository.findAll();

        return lista.stream().map(presenca -> new presencaResponse(presenca.getId(),
                presenca.getAtleta().getNome(),
                presenca.getPresente()))
                .collect(Collectors.toList());
   }
    public List<historicoPresencaResponse> getHistoricoPresencas() {
        // Busca todas as presenças
        List<presenca> todasPresencas = presencaRepository.findAll();

        // Mapeia para o DTO de resposta do histórico
        return todasPresencas.stream()
                .map(presenca -> new historicoPresencaResponse(
                        presenca.getId(),
                        presenca.getData(),
                        presenca.getPresente(),
                        presenca.getAtleta().getId(),
                        presenca.getAtleta().getNome() // Assumindo que Atleta tem um método getNome()
                ))
                .collect(Collectors.toList());
    }
    public List<presencaResponse> getAtletasComPresencaNaData(LocalDate data) {
        // Busca todos os atletas cadastrados
        List<atleta> todosAtletas = atletaRepository.findAll();

        // Busca presenças registradas na data específica
        List<presenca> presencasNaData = presencaRepository.findByData(data);

        return todosAtletas.stream().map(atleta -> {

            Optional<presenca> presenca = presencasNaData.stream()
                    .filter(p -> p.getAtleta().getId().equals(atleta.getId()))
                    .findFirst();

            return new presencaResponse(
                    atleta.getId(),
                    atleta.getNome(),
                    presenca.isPresent() ? presenca.get().getPresente() : null
            );
        }).collect(Collectors.toList());
    }



}