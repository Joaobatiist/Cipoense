package com.br.Controller;

import com.br.Entity.RelatorioDesempenho;
import com.br.Service.RelatorioDesempenhoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relatorios/desempenho")
public class RelatorioDesempenhoController {

    private final RelatorioDesempenhoService relatorioDesempenhoService;

    @Autowired
    public RelatorioDesempenhoController(RelatorioDesempenhoService relatorioDesempenhoService) {
        this.relatorioDesempenhoService = relatorioDesempenhoService;
    }

    @PostMapping

    public ResponseEntity<RelatorioDesempenho> cadastrarrelatorioDesempenho(@RequestBody RelatorioDesempenho RelatorioDesempenho) {
        RelatorioDesempenho savedRelatorio = relatorioDesempenhoService.cadastrarRelatorioDesempenho(RelatorioDesempenho);
        return new ResponseEntity<>(savedRelatorio, HttpStatus.CREATED);
    }

    // Endpoint para buscar por ID com todos os detalhes (Tecnico, Desempenho, Avaliadores)
    @GetMapping("/visualizar")

    public ResponseEntity<List<RelatorioDesempenho>> getrelatorioDesempenho() {
        try {
            List<RelatorioDesempenho> relatorio = relatorioDesempenhoService.listarRelatorioDesempenho();
            return ResponseEntity.ok(relatorio);
        } catch (Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/deletar")

    public ResponseEntity<Void> deleterelatorioDesempenho(@PathVariable Long id) {
        try {
            relatorioDesempenhoService.deleteByIdDesempenho(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/atualizar")

    public ResponseEntity<RelatorioDesempenho>atualizarrelatorioDesempenho(@PathVariable Long id, @RequestBody RelatorioDesempenho relatorioDesempenho) {
        try {
            RelatorioDesempenho relatorioDesempenhoAtualizado = relatorioDesempenhoService.atualizarRelatorioDesempenho(id, relatorioDesempenho);
            return ResponseEntity.ok(relatorioDesempenhoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Erro ao atualizar relatorio geral: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}