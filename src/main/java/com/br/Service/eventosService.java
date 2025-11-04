package com.br.Service;

import com.br.Entity.atleta;
import com.br.Entity.eventos;
import com.br.Enums.subDivisao;
import com.br.Repository.atletaRepository;
import com.br.Repository.eventosRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.HashSet;
import java.util.Optional;
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
        try {
            System.out.println("DEBUG: Tentando salvar evento: " + eventos.getDescricao());
            System.out.println("DEBUG: Data: " + eventos.getData());
            System.out.println("DEBUG: SubDivisão: " + eventos.getSubDivisao());

            eventos eventoSalvo = eventosRepository.save(eventos);

            System.out.println("DEBUG: Evento salvo com ID: " + eventoSalvo.getId());
            return eventoSalvo;
        } catch (Exception e) {
            System.err.println("ERRO no Service ao salvar evento: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
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

        // Assumindo que findEventosByAtletaVisibility está implementado no eventosRepository
        return eventosRepository.findEventosByAtletaVisibility(atleta.getId());
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
            // O Hibernate cuida dos DELETEs e INSERTs aqui
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

    //===============================================================================================
    // MÉTODOS DE ESCALAÇÃO CORRIGIDOS (Apenas Manipulação do Lado Proprietário)

    @Transactional
    public eventos escalarAtleta(UUID eventoId, UUID atletaId) {
        eventos evento = eventosRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado com o ID: " + eventoId));

        atleta atleta = atletaRepository.findById(atletaId)
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado com o ID: " + atletaId));

        // Adiciona o atleta ao evento (Lado Proprietário)
        // REMOVIDA toda a lógica de manipulação do lado 'atleta' e o save(atleta).
        evento.getAtletasEscalados().add(atleta);

        System.out.println("Evento ID: " + evento.getId());
        System.out.println("Atleta ID: " + atleta.getId());

        // saveAndFlush garante que a operação ocorra imediatamente na tabela de junção.
        return eventosRepository.saveAndFlush(evento);
    }

    @Transactional
    public eventos desescalarAtleta(UUID eventoId, UUID atletaId) {
        eventos evento = eventosRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado com o ID: " + eventoId));

        atleta atleta = atletaRepository.findById(atletaId)
                .orElseThrow(() -> new RuntimeException("Atleta não encontrado com o ID: " + atletaId));

        // Remove do lado proprietário
        evento.getAtletasEscalados().remove(atleta);

        // Nenhuma manipulação do lado inverso ou save é necessária.
        return eventosRepository.save(evento);
    }
}