
package com.br.Controller;

import com.br.Entity.Aluno;
import com.br.Service.CadastroService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List; // Use List for ordered collection, or Set if order doesn't matter
import java.util.stream.Collectors; // For stream operations

@RestController
@RequestMapping("/api")
@CrossOrigin("http://192.168.0.10:8081")
public class CadastroController {

    private final CadastroService cadastroService;

    public CadastroController(CadastroService cadastroService) {
        this.cadastroService = cadastroService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Aluno> cadastrarAlunoComResponsavel(@RequestBody Aluno aluno) {
        try {
            Aluno novoAluno = cadastroService.cadastrarAlunoComResponsavel(aluno);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoAluno);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            System.err.println("Erro interno ao cadastrar aluno: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/alunos/nomes")
    public ResponseEntity<List<String>> listarNomesDosAlunos() {
        try {

            List<Aluno> todosAlunos = cadastroService.listarTodosAlunos();


            List<String> nomesAlunos = todosAlunos.stream()
                    .map(Aluno::getNome)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(nomesAlunos);
        } catch (Exception e) {
            System.err.println("Erro ao buscar nomes dos alunos: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/alunos")
    public ResponseEntity<List<Aluno>> listarTodosAlunos() {
        try {
            List<Aluno> todosAlunos = cadastroService.listarTodosAlunos();
            return ResponseEntity.ok(todosAlunos);
        } catch (Exception e) {
            System.err.println("Erro ao buscar todos os alunos: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}