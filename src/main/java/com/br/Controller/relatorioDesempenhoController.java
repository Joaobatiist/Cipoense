package com.br.Controller;

import com.br.Entity.relatorioDesempenho;
import com.br.Service.relatorioDesempenhoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relatorios/desempenho")
public class relatorioDesempenhoController {

    private final relatorioDesempenhoService relatorioDesempenhoService;

    @Autowired
    public relatorioDesempenhoController(relatorioDesempenhoService relatorioDesempenhoService) {
        this.relatorioDesempenhoService = relatorioDesempenhoService;
    }

    @PostMapping

    public ResponseEntity<relatorioDesempenho> cadastrarrelatorioDesempenho(@RequestBody relatorioDesempenho RelatorioDesempenho) {
        relatorioDesempenho savedRelatorio = relatorioDesempenhoService.cadastrarRelatorioDesempenho(RelatorioDesempenho);
        return new ResponseEntity<>(savedRelatorio, HttpStatus.CREATED);
    }

    // Endpoint para buscar por ID com todos os detalhes (Tecnico, Desempenho, Avaliadores)
    @GetMapping("/visualizar")

    public ResponseEntity<List<relatorioDesempenho>> getrelatorioDesempenho() {
        try {
            List<relatorioDesempenho> relatorio = relatorioDesempenhoService.listarRelatorioDesempenho();
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

    public ResponseEntity<relatorioDesempenho>atualizarrelatorioDesempenho(@PathVariable Long id, @RequestBody relatorioDesempenho relatorioDesempenho) {
        try {
            relatorioDesempenho relatorioDesempenhoAtualizado = relatorioDesempenhoService.atualizarRelatorioDesempenho(id, relatorioDesempenho);
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