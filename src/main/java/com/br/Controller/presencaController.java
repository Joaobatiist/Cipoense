package com.br.Controller;

import com.br.Repository.atletaRepository;
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
// Seu serviço de presença

@RestController
@RequestMapping("/api/presenca")
public class presencaController {

    private final presencaService presencaService;

@Autowired
private atletaRepository atletaRepository;



    public presencaController(presencaService presencaService) {
        this.presencaService = presencaService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<String> registrarPresencas(@RequestBody List<presencaRequest> presencasDTO) {
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
    public ResponseEntity<List<presencaResponse>> getAtletasComPresenca(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {

        try {
            List<presencaResponse> lista = presencaService.getAtletasComPresencaNaData(data);
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            System.err.println("Erro ao buscar a lista de alunos" + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



    // NOVO ENDPOINT: Para buscar o histórico de presenças (usado na tela de histórico)
    @GetMapping("/historico")
    public ResponseEntity<List<historicoPresencaResponse>> getHistoricoPresencas() {
        try {
            List<historicoPresencaResponse> historico = presencaService.getHistoricoPresencas();
            return ResponseEntity.ok(historico);
        } catch (Exception e) {
            System.err.println("Erro ao buscar histórico de presenças: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}