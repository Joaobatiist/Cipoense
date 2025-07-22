// src/main/java/com/br/Controller/RelatorioAvaliacaoGeralController.java
package com.br.Controller;

import com.br.Service.RelatorioAvaliacaoGService;
import com.br.Entity.RelatorioAvaliacaoGeral; // Pode ser mantido se for usado para @RequestBody no PUT
import com.br.Response.AvaliacaoGeralResponse;
import com.br.Request.CriarAvaliacaoRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/relatoriogeral")
public class RelatorioAvaliacaoGeralController {

    private final RelatorioAvaliacaoGService relatorioAvaliacaoGService;

    @Autowired
    public RelatorioAvaliacaoGeralController(RelatorioAvaliacaoGService relatorioAvaliacaoGService) {
        this.relatorioAvaliacaoGService = relatorioAvaliacaoGService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<AvaliacaoGeralResponse> cadastrarRelatorioGeral(@RequestBody CriarAvaliacaoRequest request) {
        AvaliacaoGeralResponse response = relatorioAvaliacaoGService.cadastrarRelatorioGeral(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<AvaliacaoGeralResponse>> listarRelatorioGeral() {
        List<AvaliacaoGeralResponse> relatorios = relatorioAvaliacaoGService.listarRelatorioGeral();
        return ResponseEntity.ok(relatorios);
    }

    // *** ESTE É O ENDPOINT ONDE ESTAVA O ERRO DE CONSTRUTOR ***
    @GetMapping("/buscarporid/{id}")
    public ResponseEntity<AvaliacaoGeralResponse> buscarRelatorioGeralPorId(@PathVariable Long id) {
        // Busca a entidade completa pelo serviço
        Optional<RelatorioAvaliacaoGeral> relatorioOptional = relatorioAvaliacaoGService.findById(id);

        return relatorioOptional.map(relatorioGeral -> {
            // Mapeia a entidade para o DTO de resposta usando o construtor que aceita RelatorioAvaliacaoGeral
            AvaliacaoGeralResponse responseDto = new AvaliacaoGeralResponse(relatorioGeral);
            return ResponseEntity.ok(responseDto);
        }).orElse(ResponseEntity.notFound().build());
    }

    // Endpoint visualizar (se diferente de buscarporid ou se for para listagem)
    // Se o /visualizar é para um ID específico, deve ser assim:
    @GetMapping("/visualizar/{id}")
    public ResponseEntity<AvaliacaoGeralResponse> visualizarRelatorioGeralPorId(@PathVariable Long id) {
        // Reutiliza a lógica de busca por ID e conversão para DTO
        return relatorioAvaliacaoGService.findById(id)
                .map(AvaliacaoGeralResponse::new) // Usa referência de método para o construtor do DTO
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarRelatorioGeral(@PathVariable Long id) {
        relatorioAvaliacaoGService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<RelatorioAvaliacaoGeral> atualizarRelatorioGeral(
            @PathVariable Long id,
            @RequestBody RelatorioAvaliacaoGeral relatorioAvaliacaoGeralAtualizado) {
        try {
            RelatorioAvaliacaoGeral relatorioAtualizado = relatorioAvaliacaoGService.atualizarRelatorioGeral(id, relatorioAvaliacaoGeralAtualizado);
            return ResponseEntity.ok(relatorioAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}