package com.br.Service;

import com.br.Entity.eventos;
import com.br.Repository.eventosRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List; // Add import for List
import java.util.Optional; // Add import for Optional

@Service
public class eventosService {
    private final eventosRepository eventosRepository;

    public eventosService(eventosRepository eventosRepository) {
        this.eventosRepository = eventosRepository;
    }

    @Transactional
    public eventos cadastrarEvento(eventos eventos){
        return eventosRepository.save(eventos);
    }


    public List<eventos> listarTodosEventos() {
        return eventosRepository.findAll();
    }

    @Transactional
    public eventos atualizarEvento(Long id, eventos eventoAtualizado) {

        Optional<eventos> existingEventOptional = eventosRepository.findById(id);

        if (existingEventOptional.isPresent()) {
            eventos existingEvent = existingEventOptional.get();
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

    public Optional<eventos> buscarEventoPorId(Long id) {
        return eventosRepository.findById(id);
    }
}