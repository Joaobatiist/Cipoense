package com.br.Service;

import com.br.Entity.atleta;
import com.br.Entity.eventos;
import com.br.Entity.presenca;
import com.br.Enums.subDivisao; // Importar se não estiver importado
import com.br.Repository.atletaRepository;
import com.br.Repository.eventosRepository;
import com.br.Repository.presencaRepository;
import com.br.Request.eventoRequest;
import com.br.Request.presencaRequest;
import com.br.Response.historicoPresencaResponse;
import com.br.Response.presencaResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set; // Manter
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class presencaService {

    private final presencaRepository presencaRepository;
    private final atletaRepository atletaRepository;
    private final eventosRepository eventosRepository;

    public presencaService(presencaRepository presencaRepository,
                           atletaRepository atletaRepository,
                           eventosRepository eventosRepository) {
        this.presencaRepository = presencaRepository;
        this.atletaRepository = atletaRepository;
        this.eventosRepository = eventosRepository;
    }

    @Transactional
    public void registrarPresencas(List<presencaRequest> presencas) {
        for (presencaRequest dto : presencas) {
            atleta atleta = atletaRepository.findById(dto.getAtletaId())
                    .orElseThrow(() -> new RuntimeException("Atleta não encontrado com ID: " + dto.getAtletaId()));

            eventos evento = eventosRepository.findById(dto.getEventoId())
                    .orElseThrow(() -> new RuntimeException("Evento não encontrado com ID: " + dto.getEventoId()));

            Optional<presenca> presencaExistente = presencaRepository.findByAtletaAndEvento(atleta, evento);

            if (presencaExistente.isPresent()) {
                presenca presenca = presencaExistente.get();
                presenca.setPresente(dto.getPresente());
                presencaRepository.save(presenca);
            } else {
                presenca novaPresenca = new presenca();
                novaPresenca.setAtleta(atleta);
                novaPresenca.setPresente(dto.getPresente());
                novaPresenca.setEvento(evento);
                novaPresenca.setData(evento.getData());
                presencaRepository.save(novaPresenca);
            }
        }
    }

    /**
     * ABORDAGEM FINAL E IDIOMÁTICA: Confia no lado proprietário (Eventos) para gerenciar a junção.
     */
    @Transactional
    public void escalarAtletas(UUID eventoId, eventoRequest request) {
        eventos evento = eventosRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado com ID: " + eventoId));

        // 1. Limpa a coleção do lado proprietário. Isso fará com que o Hibernate gere os DELETEs.
        evento.getAtletasEscalados().clear();

        // 2. Busca os novos atletas
        List<atleta> atletasParaEscalar = atletaRepository.findAllById(request.getAtletasIds());

        if (atletasParaEscalar.size() != request.getAtletasIds().size()) {
            throw new RuntimeException("Um ou mais atletas na lista não foram encontrados.");
        }

        // 3. Adiciona os novos atletas à coleção.
        evento.getAtletasEscalados().addAll(atletasParaEscalar);

        // 4. Salva o evento. O CascadeType.ALL agora deve garantir a persistência correta na tabela eventos_atletas.
        eventosRepository.save(evento);
    }


    /**
     * CORRIGIDO: Endpoint para buscar a lista de presença para um evento específico.
     * A lógica é baseada na SubDivisão do Evento, listando TODOS os atletas dessa divisão.
     */
    @Transactional
    public List<presencaResponse> getListaPresencaParaEvento(UUID eventoId) {
        eventos evento = eventosRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado com ID: " + eventoId));

        subDivisao divisaoEvento = evento.getSubDivisao();

        // 1. Busca TODOS os atletas que pertencem à SubDivisão do Evento
        List<atleta> atletasDaDivisao = atletaRepository.findAllBySubDivisao(divisaoEvento);

        System.out.println("DEBUG: Evento ID " + eventoId + " (Divisão: " + divisaoEvento + ") tem " + atletasDaDivisao.size() + " atletas elegíveis.");

        if (atletasDaDivisao.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Busca todas as presenças já registradas para este evento
        List<presenca> presencasRegistradas = presencaRepository.findByEvento(evento);

        // 3. Mapeia para PresencaResponse, usando a lista de atletas da subdivisão
        return atletasDaDivisao.stream().map(atleta -> {

            // Tenta encontrar o registro de presença para este atleta neste evento
            Optional<presenca> presencaOpt = presencasRegistradas.stream()
                    .filter(p -> p.getAtleta().getId().equals(atleta.getId()))
                    .findFirst();

            if (presencaOpt.isPresent()) {
                presenca p = presencaOpt.get();
                return new presencaResponse(
                        p.getId(),
                        atleta.getId(),
                        atleta.getNome(),
                        p.getPresente(),
                        evento.getId(),
                        evento.getDescricao()
                );
            } else {
                // Se não houver presença registrada, retorna com 'presente: null'
                return new presencaResponse(
                        null,
                        atleta.getId(),
                        atleta.getNome(),
                        null, // null indica que a presença ainda não foi marcada
                        evento.getId(),
                        evento.getDescricao()
                );
            }
        }).collect(Collectors.toList());
    }

    // ... (Os demais métodos permanecem inalterados) ...

    @Transactional
    public List<eventos> buscarEventosAtletasEscalados(UUID atletaId) {
        List<presenca> listaEscalada = presencaRepository.findByAtletaId(atletaId);
        return listaEscalada.stream()
                .map(presenca::getEvento)
                .collect(Collectors.toList());
    }

    @Transactional
    public presenca atualizarPresenca(UUID presencaId, presenca presenca){
        Optional<presenca> presencaExistente = presencaRepository.findById(presencaId);
        if (presencaExistente.isEmpty()) {
            throw new RuntimeException("Presença não encontrada com ID: " + presencaId);
        }
        presenca presencaAtual = presencaExistente.get();
        presencaAtual.setData(presenca.getData());
        presencaAtual.setPresente(presenca.getPresente());
        presencaRepository.save(presencaAtual);
        return presencaAtual;
    }

    @Transactional
    public List<presencaResponse> getLista() {
        List <presenca> lista = presencaRepository.findAll();
        return lista.stream().map(presenca -> new presencaResponse(
                        presenca.getId(),
                        presenca.getAtleta().getId(),
                        presenca.getAtleta().getNome(),
                        presenca.getPresente(),
                        presenca.getEvento() != null ? presenca.getEvento().getId() : null,
                        presenca.getEvento() != null ? presenca.getEvento().getDescricao() : null))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<historicoPresencaResponse> getHistoricoPresencas() {
        List<presenca> todasPresencas = presencaRepository.findAll();
        return todasPresencas.stream()
                .map(presenca -> new historicoPresencaResponse(
                        presenca.getId(),
                        presenca.getData(),
                        presenca.getPresente(),
                        presenca.getAtleta().getId(),
                        presenca.getAtleta().getNome()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<presencaResponse> getAtletasComPresencaNaData(LocalDate data) {
        List<atleta> todosAtletas = atletaRepository.findAll();
        List<presenca> presencasNaData = presencaRepository.findByData(data);
        return todosAtletas.stream().map(atleta -> {
            Optional<presenca> presenca = presencasNaData.stream()
                    .filter(p -> p.getAtleta().getId().equals(atleta.getId()))
                    .findFirst();
            return new presencaResponse(
                    presenca.isPresent() ? presenca.get().getId() : null,
                    atleta.getId(),
                    atleta.getNome(),
                    presenca.isPresent() ? presenca.get().getPresente() : null,
                    presenca.isPresent() && presenca.get().getEvento() != null ? presenca.get().getEvento().getId() : null,
                    presenca.isPresent() && presenca.get().getEvento() != null ? presenca.get().getEvento().getDescricao() : null
            );
        }).collect(Collectors.toList());
    }
}