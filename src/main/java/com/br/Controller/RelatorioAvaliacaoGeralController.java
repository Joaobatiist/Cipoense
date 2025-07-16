package com.br.Controller;

import com.br.Entity.RelatorioAvaliacaoGeral;
import com.br.Service.RelatorioAvaliacaoGService;
import com.br.Request.CriarAvaliacaoRequest; // Importar o DTO de Requisição
import com.br.Response.AvaliacaoGeralResponse; // Importar o DTO de Resposta
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid; // Para validar o DTO de Requisição

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/relatoriogeral")
public class RelatorioAvaliacaoGeralController {

    private final RelatorioAvaliacaoGService relatorioAvaliacaoGService;

    @Autowired
    public RelatorioAvaliacaoGeralController(RelatorioAvaliacaoGService relatorioAvaliacaoGService) {
        this.relatorioAvaliacaoGService = relatorioAvaliacaoGService; // Injeção de dependência correta
    }


    @PostMapping("/cadastrar")
    public ResponseEntity<AvaliacaoGeralResponse> cadastrarRelatorioGeral(@RequestBody @Valid CriarAvaliacaoRequest request) {
        AvaliacaoGeralResponse response = relatorioAvaliacaoGService.cadastrarRelatorioGeral(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<RelatorioAvaliacaoGeral>> listarTodosRelatoriosGerais() {
        try {
            List<RelatorioAvaliacaoGeral> relatorios = relatorioAvaliacaoGService.listarRelatorioGeral();
            return ResponseEntity.ok(relatorios);
        } catch (Exception e){
            System.err.println("Erro ao listar todos os relatórios gerais: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Melhor retornar 500 para erro interno
        }
    }

    @GetMapping("/buscarporid") // Novo método para buscar por ID
    public ResponseEntity<RelatorioAvaliacaoGeral> getRelatorioGeralById(@PathVariable Long id) {
        Optional<RelatorioAvaliacaoGeral> relatorio = relatorioAvaliacaoGService.findById(id);
        return relatorio.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/deletar") // CORREÇÃO: Adicionado {id} ao path

    public ResponseEntity<Void> deleteRelatorioGeral(@PathVariable Long id) {
        try {
            relatorioAvaliacaoGService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.err.println("Erro ao deletar relatório geral com ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/atualizar") // CORREÇÃO: Adicionado {id} ao path
    public ResponseEntity<RelatorioAvaliacaoGeral> atualizarRelatorioGeral(@PathVariable Long id, @RequestBody RelatorioAvaliacaoGeral relatorioGeral) {
        try {
            RelatorioAvaliacaoGeral relatorioGeralAtualizado = relatorioAvaliacaoGService.atualizarRelatorioGeral(id, relatorioGeral);
            return ResponseEntity.ok(relatorioGeralAtualizado);
        } catch (RuntimeException e) {
            System.err.println("Erro ao atualizar relatório geral com ID " + id + ": " + e.getMessage());
            return ResponseEntity.notFound().build(); // Retorna 404 se o item não for encontrado
        } catch (Exception e) {
            System.err.println("Erro inesperado ao atualizar relatório geral: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
