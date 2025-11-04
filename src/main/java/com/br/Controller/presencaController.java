package com.br.Controller;

import com.br.Entity.presenca;
import com.br.Request.presencaRequest;
import com.br.Response.historicoPresencaResponse;
import com.br.Response.presencaResponse;
import com.br.Service.presencaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

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

    @GetMapping("/atletas")
    public ResponseEntity<List<presencaResponse>> getAtletasComPresenca(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        try {
            List<presencaResponse> lista = presencaService.getAtletasComPresencaNaData(data);
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
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