package com.br.Controller;

import com.br.Entity.analiseIa;
import com.br.Repository.analiseIaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analises")
public class analiseIaController {

    @Autowired
    private analiseIaRepository analiseIARepository;

    @GetMapping("/atleta/{email}")
    public ResponseEntity<List<analiseIa>> getAnalisesByAtletaEmail(@PathVariable String email) {
        List<analiseIa> analises = analiseIARepository.findByAtletaEmailOrderByDataAnaliseDesc(email);

        // Retorna a lista de análises, que pode ser vazia, com status 200 OK.
        // O Spring Boot irá serializar a lista (vazia ou não) para um JSON válido.
        return ResponseEntity.ok(analises);
    }
}