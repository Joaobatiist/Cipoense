package com.br.Controller;

import com.br.Repository.AlunoRepository;
import com.br.Repository.CoordenadorRepository;
import com.br.Repository.SupervisorRepository;
import com.br.Repository.TecnicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api") // O request mapping base para este controller
public class UsuarioController {

    @Autowired
    private AlunoRepository alunoRepository;
    @Autowired
    private CoordenadorRepository coordenadorRepository;
    @Autowired
    private SupervisorRepository supervisorRepository;
    @Autowired
    private TecnicoRepository tecnicoRepository;

    // DTO simples para retornar a lista de usuários para seleção no frontend
    public static class UserForSelection {
        public Long id;
        public String nome;
        public String tipo; // Para diferenciar Aluno, Coordenador, Supervisor, Tecnico

        public UserForSelection(Long id, String nome, String tipo) {
            this.id = id;
            this.nome = nome;
            this.tipo = tipo;
        }

        // Getters para serialização JSON (Lombok @Data faria isso automaticamente)
        public Long getId() { return id; }
        public String getNome() { return nome; }
        public String getTipo() { return tipo; }
    }

    @GetMapping("/usuarios-para-comunicado")
    public ResponseEntity<List<UserForSelection>> getUsuariosForComunicado() {
        List<UserForSelection> users = new ArrayList<>();

        // Adiciona Alunos
        alunoRepository.findAll().forEach(a -> users.add(new UserForSelection(a.getId(), a.getNome(), "Aluno")));
        // Adiciona Coordenadores
        coordenadorRepository.findAll().forEach(c -> users.add(new UserForSelection(c.getId(), c.getNome(), "Coordenador")));
        // Adiciona Supervisores
        supervisorRepository.findAll().forEach(s -> users.add(new UserForSelection(s.getId(), s.getNome(), "Supervisor")));
        // Adiciona Técnicos
        tecnicoRepository.findAll().forEach(t -> users.add(new UserForSelection(t.getId(), t.getNome(), "Tecnico")));

        return ResponseEntity.ok(users);
    }
}