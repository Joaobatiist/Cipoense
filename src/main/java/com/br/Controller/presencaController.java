package com.br.Controller;

import com.br.Request.eventoRequest; // Usando o nome do DTO que você forneceu
import com.br.Request.presencaRequest;
import com.br.Response.historicoPresencaResponse;
import com.br.Response.presencaResponse;
import com.br.Service.presencaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/presenca")
public class presencaController {

    private final presencaService presencaService;

    @Autowired
    public presencaController(presencaService presencaService) {
        this.presencaService = presencaService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<String> registrarPresencas(@RequestBody List<presencaRequest> presencasDTO) {
        try {
            presencaService.registrarPresencas(presencasDTO);
            return ResponseEntity.ok("Presenças registradas com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao registrar presenças: " + e.getMessage());
        }
    }

    /**
     * ENDPOINT: Escala (vincula/desvincula) atletas a um evento.
     */
    @PutMapping("/evento/{eventoId}/escalar")
    public ResponseEntity<String> escalarAtletas(@PathVariable UUID eventoId, @RequestBody eventoRequest request) {
        try {
            presencaService.escalarAtletas(eventoId, request);
            return ResponseEntity.ok("Atletas escalados para o evento com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao escalar atletas: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao escalar atletas: " + e.getMessage());
        }
    }


    /**
     * Endpoint para buscar a lista de presença para um evento específico.
     */
    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<presencaResponse>> getListaPresencaParaEvento(@PathVariable UUID eventoId) {
        try {
            List<presencaResponse> lista = presencaService.getListaPresencaParaEvento(eventoId);
            return ResponseEntity.ok(lista);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/historico")
    public ResponseEntity<List<historicoPresencaResponse>> getHistoricoPresencas() {
        try {
            List<historicoPresencaResponse> historico = presencaService.getHistoricoPresencas();
            return ResponseEntity.ok(historico);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}