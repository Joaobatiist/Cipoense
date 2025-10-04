
package com.br.Controller;

import com.br.Entity.atleta;
import com.br.Service.cadastroService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class cadastroController {

    private final cadastroService cadastroService;

    public cadastroController(cadastroService cadastroService) {
        this.cadastroService = cadastroService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<atleta> cadastrarAtletaComResponsavel(@RequestBody atleta atleta) {
        try {
            atleta novoAtleta = cadastroService.cadastrarAtletaComResponsavel(atleta);
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