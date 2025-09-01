package com.br.Controller;

import com.br.Entity.relatorioTaticoPsicologico;
import com.br.Service.relatorioTaticoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relatorios/tatico")
public class relatorioTaticoController {

    private final relatorioTaticoService relatorioTaticoService;

    @Autowired
    public relatorioTaticoController(relatorioTaticoService relatorioTaticoService) {
        this.relatorioTaticoService = relatorioTaticoService;
    }

    @PostMapping

    public ResponseEntity<relatorioTaticoPsicologico> cadastrarrelatorioTatico(@RequestBody relatorioTaticoPsicologico RelatorioTaticoPsicologico) {
        relatorioTaticoPsicologico savedRelatorio = relatorioTaticoService.cadastrarRelatorioTatico(RelatorioTaticoPsicologico);
        return new ResponseEntity<>(savedRelatorio, HttpStatus.CREATED);
    }

    // Endpoint para buscar por ID com todos os detalhes (Tecnico, Tatico, Avaliadores)
    @GetMapping("/visualizar")

    public ResponseEntity<List<relatorioTaticoPsicologico>> getrelatorioTatico() {
        try {
            List<relatorioTaticoPsicologico> relatorio = relatorioTaticoService.listarRelatorioTatico();
            return ResponseEntity.ok(relatorio);
        } catch (Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/deletar")

    public ResponseEntity<Void> deleterelatorioTatico(@PathVariable Long id) {
        try {
            relatorioTaticoService.deleteByIdTatico(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/atualizar")

    public ResponseEntity<relatorioTaticoPsicologico>atualizarrelatorioTatico(@PathVariable Long id, @RequestBody relatorioTaticoPsicologico relatorioTatico) {
        try {
            relatorioTaticoPsicologico relatorioTaticoAtualizado = relatorioTaticoService.atualizarRelatorioTatico(id, relatorioTatico);
            return ResponseEntity.ok(relatorioTaticoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Erro ao atualizar relatorio geral: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}