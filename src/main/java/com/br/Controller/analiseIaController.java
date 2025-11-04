package com.br.Controller;

import com.br.Entity.analiseIa;
import com.br.Repository.analiseIaRepository;
import com.br.Service.analiseIaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/analises")
public class analiseIaController {

    @Autowired
    private analiseIaRepository analiseIARepository;

    @Autowired
    private final analiseIaService analiseIaService;

   public analiseIaController(analiseIaService analiseIaService) {
       this.analiseIaService = analiseIaService;
   }

    @GetMapping("/atleta/{email}")
    public ResponseEntity<List<analiseIa>> getAnalisesByAtletaEmail(@PathVariable String email) {
        List<analiseIa> analises = analiseIARepository.findByAtletaEmailOrderByDataAnaliseDesc(email);

        // Retorna a lista de análises, que pode ser vazia, com status 200 OK.
        // O Spring Boot irá serializar a lista (vazia ou não) para um JSON válido.
        return ResponseEntity.ok(analises);
    }


    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam UUID id) {

        analiseIaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/Atualizar/{id}")
    public ResponseEntity<analiseIa> atualizarRespostaIa(@PathVariable UUID id, @RequestBody analiseIa analiseIa) {
       try {
           analiseIa = analiseIaService.atualizarAnalise(id, analiseIa);
           return ResponseEntity.ok(analiseIa);
       } catch (RuntimeException e) {
           return ResponseEntity.notFound().build();
       } catch (Exception e) {
           System.err.println("Erro ao atualizar Resposta IA: " + e.getMessage());
           e.printStackTrace();
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
       }
    }
}