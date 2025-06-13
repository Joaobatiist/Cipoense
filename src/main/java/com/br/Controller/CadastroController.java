
package com.br.Controller;

import com.br.Entity.Atleta;
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
    public ResponseEntity<Atleta> cadastrarAtletaComResponsavel(@RequestBody Atleta atleta) {
        try {
            Atleta novoAtleta = cadastroService.cadastrarAtletaComResponsavel(atleta);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoAtleta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            System.err.println("Erro interno ao cadastrar atleta: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



}