package com.br.Service;

import com.br.Entity.atleta;
import com.br.Entity.eventos;
import com.br.Enums.subDivisao;
import com.br.Repository.atletaRepository;
import com.br.Repository.eventosRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List; // Add import for List
import java.util.Optional; // Add import for Optional
import java.util.UUID;

@Service
public class eventosService {
    private final eventosRepository eventosRepository;
    private final atletaRepository atletaRepository;

    public eventosService(eventosRepository eventosRepository, atletaRepository atletaRepository) {
        this.eventosRepository = eventosRepository;
        this.atletaRepository = atletaRepository;
    }

    @Transactional
    public eventos cadastrarEvento(eventos eventos){
        return eventosRepository.save(eventos);
    }

    //===============================================================================================
    //Métodos para busca e listagem dos eventos

    public List<eventos> listarTodosEventos() {
        return eventosRepository.findAll();
    }

    public Optional<eventos> buscarEventoPorId(UUID id) {
        return eventosRepository.findById(id);
    }

    public List<eventos> listarEventosVisiveisParaAtleta(String emailAtleta) {
        // 1. Encontrar o Atleta pelo Email (e, consequentemente, seu UUID)
        atleta atleta = atletaRepository.findByEmail(emailAtleta)
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado com o email: " + emailAtleta));

        return eventosRepository.findEventosByAtletaVisibility(atleta.getId()); // Assumindo que getId() retorna o UUID
    }

    // ====================================================================================

    @Transactional
    public eventos atualizarEvento(UUID id, eventos eventoAtualizado) {
        Optional<eventos> existingEventOptional = eventosRepository.findById(id);
        if (existingEventOptional.isPresent()) {
            eventos existingEvent = existingEventOptional.get();
            // Update fields
            existingEvent.setData(eventoAtualizado.getData());
            existingEvent.setDescricao(eventoAtualizado.getDescricao());
            existingEvent.setProfessor(eventoAtualizado.getProfessor());
            existingEvent.setLocal(eventoAtualizado.getLocal());
            existingEvent.setHorario(eventoAtualizado.getHorario());
            existingEvent.setAtletasEscalados(eventoAtualizado.getAtletasEscalados());
            return eventosRepository.save(existingEvent); // Save updated event
        } else {
            // Handle not found, e.g., throw an exception
            throw new RuntimeException("Evento não encontrado com o ID: " + id);
        }
    }

    @Transactional
    public void deletarEvento(UUID id) {
        if (!eventosRepository.existsById(id)) {
            throw new RuntimeException("Evento não encontrado com o ID: " + id);
        }
        eventosRepository.deleteById(id);
    }

    //Métodos para listagem de atletas

    @Transactional
    public List<atleta> listarAtletasElegiveisPorEvento(UUID eventoId) {
        eventos evento = eventosRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado com o ID: " + eventoId));
        subDivisao divisao = evento.getSubDivisao();
        return atletaRepository.findAllBySubDivisao(divisao);
    }

    //Métodos para escalação do Atleta

    @Transactional
    public eventos escalarAtleta(UUID eventoId, UUID atletaId) {
        eventos evento = eventosRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado com o ID: " + eventoId));

        atleta atleta = atletaRepository.findById(atletaId)
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado com o ID: " + atletaId));

        // Adiciona o atleta ao evento
        evento.getAtletasEscalados().add(atleta);

        // Garante consistência bidirecional — adicione este método na entidade atleta (explico abaixo)
        atleta.getEventos().add(evento);

        System.out.println("Evento ID: " + evento.getId());
        System.out.println("Atleta ID: " + atleta.getId());
        // Força o Hibernate a gerar o INSERT na tabela intermediária imediatamente
        return eventosRepository.saveAndFlush(evento);
    }

    @Transactional
    public eventos desescalarAtleta(UUID eventoId, UUID atletaId) {
        eventos evento = eventosRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado com o ID: " + eventoId));

        atleta atleta = atletaRepository.findById(atletaId)
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado com o ID: " + atletaId));

        evento.getAtletasEscalados().remove(atleta);

        return eventosRepository.save(evento);
    }
}