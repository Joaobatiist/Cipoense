package com.br.Controller;

import com.br.Service.relatorioAvaliacaoGService;
import com.br.Entity.relatorioAvaliacaoGeral;
import com.br.Response.avaliacaoGeralResponse;
import com.br.Request.criarAvaliacaoRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/relatoriogeral")
public class relatorioAvaliacaoGeralController {

    private final relatorioAvaliacaoGService relatorioAvaliacaoGService;

    @Autowired
    public relatorioAvaliacaoGeralController(relatorioAvaliacaoGService relatorioAvaliacaoGService) {
        this.relatorioAvaliacaoGService = relatorioAvaliacaoGService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<avaliacaoGeralResponse> cadastrarRelatorioGeral(@RequestBody criarAvaliacaoRequest request) {
        try {
            avaliacaoGeralResponse response = relatorioAvaliacaoGService.cadastrarRelatorioGeral(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            // Captura qualquer exceção do serviço, incluindo erros da IA
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new avaliacaoGeralResponse()); // Retorna um objeto vazio ou com mensagem de erro
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<avaliacaoGeralResponse>> listarRelatorioGeral() {
        List<avaliacaoGeralResponse> relatorios = relatorioAvaliacaoGService.listarRelatorioGeral();
        return ResponseEntity.ok(relatorios);
    }

    @GetMapping("/buscarporid/{id}")
    public ResponseEntity<avaliacaoGeralResponse> buscarRelatorioGeralPorId(@PathVariable Long id) {
        Optional<relatorioAvaliacaoGeral> relatorioOptional = relatorioAvaliacaoGService.findById(id);

        return relatorioOptional.map(relatorioGeral -> {
            avaliacaoGeralResponse responseDto = new avaliacaoGeralResponse(relatorioGeral);
            return ResponseEntity.ok(responseDto);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/visualizar/{id}")
    public ResponseEntity<avaliacaoGeralResponse> visualizarRelatorioGeralPorId(@PathVariable Long id) {
        return relatorioAvaliacaoGService.findById(id)
                .map(avaliacaoGeralResponse::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/deletarporid/{id}")
    public ResponseEntity<Void> deletarRelatorioGeral(@PathVariable Long id) {
        relatorioAvaliacaoGService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<relatorioAvaliacaoGeral> atualizarRelatorioGeral(
            @PathVariable Long id,
            @RequestBody relatorioAvaliacaoGeral relatorioAvaliacaoGeralAtualizado) {
        try {
            relatorioAvaliacaoGeral relatorioAtualizado = relatorioAvaliacaoGService.atualizarRelatorioGeral(id, relatorioAvaliacaoGeralAtualizado);
            return ResponseEntity.ok(relatorioAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}