package com.br.Controller;

import com.br.Entity.Eventos;
import com.br.Service.EventosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class EventosCrontroller {

    private final EventosService eventosService;

    public EventosCrontroller(EventosService eventosService) { // Typo in constructor name
        this.eventosService = eventosService;
    }

    @PostMapping("/eventos")
    public ResponseEntity<Eventos> cadastrarEvento(@RequestBody Eventos eventos) {
        try {
            Eventos novoEvento = eventosService.cadastrarEvento(eventos);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoEvento); // Return 201 Created for new resources
        } catch (Exception e) {
            System.err.println("Erro ao cadastrar evento: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/eventos")
    public ResponseEntity<List<Eventos>> listarTodosEventos() {
        try {
            List<Eventos> eventos = eventosService.listarTodosEventos();
            return ResponseEntity.ok(eventos);
        } catch (Exception e) {
            System.err.println("Erro ao listar eventos: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/eventos/{id}")
    public ResponseEntity<Eventos> buscarEventoPorId(@PathVariable Long id) {
        try {
            Optional<Eventos> evento = eventosService.buscarEventoPorId(id);
            return evento.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build()); // Return 404 if not found
        } catch (Exception e) {
            System.err.println("Erro ao buscar evento por ID: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/eventos/{id}")
    public ResponseEntity<Eventos> atualizarEvento(@PathVariable Long id, @RequestBody Eventos eventos) {
        try {
            Eventos eventoAtualizado = eventosService.atualizarEvento(id, eventos);
            return ResponseEntity.ok(eventoAtualizado);
        } catch (RuntimeException e) { // Catch specific exception from service
            return ResponseEntity.notFound().build(); // Or 400 Bad Request if validation fails
        } catch (Exception e) {
            System.err.println("Erro ao atualizar evento: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/eventos/{id}")
    public ResponseEntity<Void> deletarEvento(@PathVariable Long id) {
        try {
            eventosService.deletarEvento(id);
            return ResponseEntity.noContent().build(); // Return 204 No Content for successful deletion
        } catch (RuntimeException e) { // Catch specific exception from service
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Erro ao deletar evento: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}