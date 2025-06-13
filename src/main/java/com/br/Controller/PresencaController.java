package com.br.Controller;

import com.br.Entity.Presenca;
import com.br.Repository.AtletaRepository;
import com.br.Request.PresencaRequest;
import com.br.Service.PresencaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
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
    public ResponseEntity<List<PresencaController.User>> getUsuariosForComunicado() {
        List<PresencaController.User> users = new ArrayList<>();

        atletaRepository.findAll().forEach(a -> users.add(new PresencaController.User(a.getId(), a.getNome(), "Atleta")));
        return ResponseEntity.ok(users);
    }


}