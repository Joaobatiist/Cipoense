package com.br.Controller;

import com.br.Entity.RelatorioTaticoPsicologico;
import com.br.Entity.RelatorioTaticoPsicologico;
import com.br.Service.RelatorioTaticoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/relatorios/tatico")
public class RelatorioTaticoController {

    private final RelatorioTaticoService relatorioTaticoService;

    @Autowired
    public RelatorioTaticoController(RelatorioTaticoService relatorioTaticoService) {
        this.relatorioTaticoService = relatorioTaticoService;
    }

    @PostMapping

    public ResponseEntity<RelatorioTaticoPsicologico> cadastrarrelatorioTatico(@RequestBody RelatorioTaticoPsicologico RelatorioTaticoPsicologico) {
        RelatorioTaticoPsicologico savedRelatorio = relatorioTaticoService.cadastrarRelatorioTatico(RelatorioTaticoPsicologico);
        return new ResponseEntity<>(savedRelatorio, HttpStatus.CREATED);
    }

    // Endpoint para buscar por ID com todos os detalhes (Tecnico, Tatico, Avaliadores)
    @GetMapping("/visualizar")

    public ResponseEntity<List<RelatorioTaticoPsicologico>> getrelatorioTatico() {
        try {
            List<RelatorioTaticoPsicologico> relatorio = relatorioTaticoService.listarRelatorioTatico();
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

    public ResponseEntity<RelatorioTaticoPsicologico>atualizarrelatorioTatico(@PathVariable Long id, @RequestBody RelatorioTaticoPsicologico relatorioTatico) {
        try {
            RelatorioTaticoPsicologico relatorioTaticoAtualizado = relatorioTaticoService.atualizarRelatorioTatico(id, relatorioTatico);
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