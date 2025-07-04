package com.br.Controller;

import com.br.Entity.Presenca;
import com.br.Repository.AtletaRepository;
import com.br.Request.PresencaRequest;
import com.br.Response.HistoricoPresencaResponse;
import com.br.Response.PresencaResponse;
import com.br.Service.PresencaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hibernate.internal.util.collections.ArrayHelper.forEach;
// Seu serviço de presença

@RestController
@RequestMapping("/api/presenca")
public class PresencaController {

    private final PresencaService presencaService;

@Autowired
private AtletaRepository atletaRepository;



    public PresencaController(PresencaService presencaService) {
        this.presencaService = presencaService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<String> registrarPresencas(@RequestBody List<PresencaRequest> presencasDTO) {
        try {
            presencaService.registrarPresencas(presencasDTO);
            return ResponseEntity.ok("Presenças registradas com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao registrar presenças: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao registrar presenças: " + e.getMessage());
        }
    }
    public static class User {
        public Long id;
        public String nome;
        public String tipo; // Para diferenciar Aluno, Coordenador, Supervisor, Tecnico

        public User(Long id, String nome, String tipo) {
            this.id = id;
            this.nome = nome;
            this.tipo = tipo;
        }

        // Getters para serialização JSON (Lombok @Data faria isso automaticamente)
        public Long getId() { return id; }
        public String getNome() { return nome; }
        public String getTipo() { return tipo; }
    }

    @GetMapping("/atletas")
    public ResponseEntity<List<PresencaResponse>> getAtletasComPresenca(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {

        try {
            List<PresencaResponse> lista = presencaService.getAtletasComPresencaNaData(data);
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            System.err.println("Erro ao buscar a lista de alunos" + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



    // NOVO ENDPOINT: Para buscar o histórico de presenças (usado na tela de histórico)
    @GetMapping("/historico")
    public ResponseEntity<List<HistoricoPresencaResponse>> getHistoricoPresencas() {
        try {
            List<HistoricoPresencaResponse> historico = presencaService.getHistoricoPresencas();
            return ResponseEntity.ok(historico);
        } catch (Exception e) {
            System.err.println("Erro ao buscar histórico de presenças: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}