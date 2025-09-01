package com.br.Controller;


import com.br.Entity.estoque;
import com.br.Service.estoqueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class estoqueController {


    private final estoqueService estoqueService;

    public estoqueController(estoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }


    @PostMapping("/estoque")
    public ResponseEntity<estoque> cadastrarItens (@RequestBody estoque estoque) {
        try {
            estoque item = estoqueService.cadastrarItem(estoque);
            return ResponseEntity.status(HttpStatus.CREATED).body(item); // Return 201 Created for new resources
        } catch (Exception e) {
            System.err.println("Erro ao cadastrar Item: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    @GetMapping("/estoque")
    public ResponseEntity<List<estoque>> listarItens(){
       try{
          List<estoque> estoque = estoqueService.listarEstoques();
          return ResponseEntity.ok(estoque);
       } catch (Exception e){
           System.err.println("Erro ao listar item: " + e.getMessage());
           e.printStackTrace();
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/estoque/{id}")
    public ResponseEntity<estoque> buscarEstoque(@PathVariable("id") Long id){
        try {
            Optional<estoque> item = estoqueService.buscaEstoque(id);
            return item.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.err.println("Erro ao buscar item por ID: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PutMapping("/estoque/{id}")
    public ResponseEntity<estoque> atualizarEstoque(@PathVariable Long id, @RequestBody estoque item){
        try {
           estoque estoque = estoqueService.atualizarItem(id, item);
            return ResponseEntity.ok(estoque);
        } catch (RuntimeException e) { // Catch specific exception from service
            return ResponseEntity.notFound().build(); // Or 400 Bad Request if validation fails
        } catch (Exception e) {
            System.err.println("Erro ao atualizar item: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @DeleteMapping("/estoque/{id}")
    public ResponseEntity<Void> deletarEvento(@PathVariable Long id) {
        try {
            estoqueService.deletarItem(id);
            return ResponseEntity.noContent().build(); // Return 204 No Content for successful deletion
        } catch (RuntimeException e) { // Catch specific exception from service
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Erro ao deletar item: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
