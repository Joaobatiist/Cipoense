package com.br.Controller;

import com.br.Repository.atletaRepository;
import com.br.Repository.funcionarioRepository; // Novo e único repositório para funcionários
import com.br.Entity.funcionario; // Importação da entidade Funcionario
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api") // O request mapping base para este controller
public class usuarioController {

    // Apenas os repositórios necessários: Atleta e Funcionario (unificado)
    @Autowired
    private atletaRepository atletaRepository;
    @Autowired
    private funcionarioRepository funcionarioRepository;


    // Classe de resposta inalterada
    public static class UserForSelection {
        public UUID id;
        public String nome;
        public String tipo; // Para diferenciar Atleta, Coordenador, Supervisor, Tecnico

        public UserForSelection(UUID id, String nome, String tipo) {
            this.id = id;
            this.nome = nome;
            this.tipo = tipo;
        }

        // Getters para serialização JSON
        public UUID getId() { return id; }
        public String getNome() { return nome; }
        public String getTipo() { return tipo; }
    }

    @GetMapping("/usuarios-para-comunicado")
    public ResponseEntity<List<UserForSelection>> getUsuariosForComunicado() {
        List<UserForSelection> users = new ArrayList<>();

        // 1. Adiciona Atletas
        atletaRepository.findAll().forEach(a -> users.add(new UserForSelection(a.getId(), a.getNome(), "Atleta")));

        // 2. Adiciona Funcionários (Unificado)
        funcionarioRepository.findAll().forEach(f ->
                users.add(new UserForSelection(f.getId(), f.getNome(), f.getRole().name()))
        );

        return ResponseEntity.ok(users);
    }
}