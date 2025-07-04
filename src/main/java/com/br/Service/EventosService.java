package com.br.Service;

import com.br.Entity.Eventos;
import com.br.Repository.EventosRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List; // Add import for List
import java.util.Optional; // Add import for Optional

@Service
public class EventosService {
    private final EventosRepository eventosRepository;

    public EventosService(EventosRepository eventosRepository) {
        this.eventosRepository = eventosRepository;
    }

    @Transactional
    public Eventos cadastrarEvento(Eventos eventos){
        return eventosRepository.save(eventos);
    }


    public List<Eventos> listarTodosEventos() {
        return eventosRepository.findAll();
    }

    @Transactional
    public Eventos atualizarEvento(Long id, Eventos eventoAtualizado) {

        Optional<Eventos> existingEventOptional = eventosRepository.findById(id);

        if (existingEventOptional.isPresent()) {
            Eventos existingEvent = existingEventOptional.get();
            // Update fields
            existingEvent.setData(eventoAtualizado.getData());
            existingEvent.setDescricao(eventoAtualizado.getDescricao());
            existingEvent.setProfessor(eventoAtualizado.getProfessor());
            existingEvent.setLocal(eventoAtualizado.getLocal());
            existingEvent.setHorario(eventoAtualizado.getHorario());
            return eventosRepository.save(existingEvent); // Save updated event
        } else {
            // Handle not found, e.g., throw an exception
            throw new RuntimeException("Evento não encontrado com o ID: " + id);
        }
    }

    @Transactional
    public void deletarEvento(Long id) {
        if (!eventosRepository.existsById(id)) {
            throw new RuntimeException("Evento não encontrado com o ID: " + id);
        }
        eventosRepository.deleteById(id);
    }

    public Optional<Eventos> buscarEventoPorId(Long id) {
        return eventosRepository.findById(id);
    }
}