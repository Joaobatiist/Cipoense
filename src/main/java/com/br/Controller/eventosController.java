package com.br.Controller;

import com.br.Entity.atleta;
import com.br.Entity.eventos;
import com.br.Service.eventosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication; // Para verificar o perfil
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder; // Para obter a autenticação
import java.security.Principal; // Para obter o ID/Nome do usuário logado

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class eventosController { // Nome da classe original mantido

    private final eventosService eventosService;

    public eventosController(eventosService eventosService) {
        this.eventosService = eventosService;
    }

    // --- 1. CRUD BÁSICO ---

    @PostMapping("/eventos")
    public ResponseEntity<eventos> cadastrarEvento(@RequestBody eventos eventos) {
        try {
            eventos novoEvento = eventosService.cadastrarEvento(eventos);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoEvento);
        } catch (Exception e) {
            System.err.println("Erro ao cadastrar evento: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/eventos")
    public ResponseEntity<List<eventos>> listarEventos(Principal principal) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean isManager = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("TECNICO") || role.equals("COORDENADOR") || role.equals("SUPERVISOR"));

            List<eventos> listaEventos;
            String emailAtleta = principal.getName();

            if (isManager) {
                listaEventos = eventosService.listarTodosEventos();
            } else {
                listaEventos = eventosService.listarEventosVisiveisParaAtleta(emailAtleta);
            }
            return ResponseEntity.ok(listaEventos);
        } catch (RuntimeException e) {
            // Captura exceções de "Atleta não encontrado" ou "Evento não encontrado"
            System.err.println("Erro de lógica ou recurso não encontrado ao listar eventos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            System.err.println("Erro interno do servidor ao listar eventos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/eventos/{id}")
    public ResponseEntity<eventos> buscarEventoPorId(@PathVariable UUID id) {
        try {
            Optional<eventos> evento = eventosService.buscarEventoPorId(id);
            return evento.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.err.println("Erro ao buscar evento por ID: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/eventos/{id}")
    public ResponseEntity<eventos> atualizarEvento(@PathVariable UUID id, @RequestBody eventos eventos) {
        try {
            eventos eventoAtualizado = eventosService.atualizarEvento(id, eventos);
            return ResponseEntity.ok(eventoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Erro ao atualizar evento: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/eventos/{id}")
    public ResponseEntity<Void> deletarEvento(@PathVariable UUID id) {
        try {
            eventosService.deletarEvento(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Erro ao deletar evento: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- 3. GESTÃO DE ESCALAÇÃO ---

    @GetMapping("/eventos/{eventoId}/elegiveis")
    public ResponseEntity<List<atleta>> listarAtletasElegiveis(@PathVariable UUID eventoId) {
        try {
            List<atleta> atletas = eventosService.listarAtletasElegiveisPorEvento(eventoId);
            return ResponseEntity.ok(atletas);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/eventos/{eventoId}/escalar/{atletaId}")
    public ResponseEntity<eventos> escalarAtleta(
            @PathVariable UUID eventoId,
            @PathVariable UUID atletaId)
    {
        try {
            eventos eventoAtualizado = eventosService.escalarAtleta(eventoId, atletaId);
            return ResponseEntity.ok(eventoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/eventos/{eventoId}/escalar/{atletaId}")
    public ResponseEntity<eventos> desescalarAtleta(
            @PathVariable UUID eventoId,
            @PathVariable UUID atletaId)
    {
        try {
            eventos eventoAtualizado = eventosService.desescalarAtleta(eventoId, atletaId);
            return ResponseEntity.ok(eventoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}