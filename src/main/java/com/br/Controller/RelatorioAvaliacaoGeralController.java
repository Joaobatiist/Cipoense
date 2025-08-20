package com.br.Controller;

import com.br.Service.RelatorioAvaliacaoGService;
import com.br.Entity.RelatorioAvaliacaoGeral;
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
        try {
            AvaliacaoGeralResponse response = relatorioAvaliacaoGService.cadastrarRelatorioGeral(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            // Captura qualquer exceção do serviço, incluindo erros da IA
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AvaliacaoGeralResponse()); // Retorna um objeto vazio ou com mensagem de erro
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<AvaliacaoGeralResponse>> listarRelatorioGeral() {
        List<AvaliacaoGeralResponse> relatorios = relatorioAvaliacaoGService.listarRelatorioGeral();
        return ResponseEntity.ok(relatorios);
    }

    @GetMapping("/buscarporid/{id}")
    public ResponseEntity<AvaliacaoGeralResponse> buscarRelatorioGeralPorId(@PathVariable Long id) {
        Optional<RelatorioAvaliacaoGeral> relatorioOptional = relatorioAvaliacaoGService.findById(id);

        return relatorioOptional.map(relatorioGeral -> {
            AvaliacaoGeralResponse responseDto = new AvaliacaoGeralResponse(relatorioGeral);
            return ResponseEntity.ok(responseDto);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/visualizar/{id}")
    public ResponseEntity<AvaliacaoGeralResponse> visualizarRelatorioGeralPorId(@PathVariable Long id) {
        return relatorioAvaliacaoGService.findById(id)
                .map(AvaliacaoGeralResponse::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/deletarporid/{id}")
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