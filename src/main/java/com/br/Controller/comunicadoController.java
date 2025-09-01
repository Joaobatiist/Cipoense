package com.br.Controller;

import com.br.Response.comunicadoResponse;
import com.br.Request.comunicadoRequest;
import com.br.Service.comunicadoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Importe este
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comunicados")
public class comunicadoController {


    private final comunicadoService comunicadoService;

    public comunicadoController(comunicadoService comunicadoService) {
        this.comunicadoService = comunicadoService;
    }


    @PostMapping
    public ResponseEntity<comunicadoResponse> criarComunicado(@RequestBody comunicadoRequest dto) {
        comunicadoResponse novoComunicado = comunicadoService.criarComunicado(dto);
        return new ResponseEntity<>(novoComunicado, HttpStatus.CREATED);
    }

    // Todos podem listar
    @GetMapping
    public ResponseEntity<List<comunicadoResponse>> getAllComunicados() {
        List<comunicadoResponse> comunicados = comunicadoService.buscarTodosComunicados();
        return new ResponseEntity<>(comunicados, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<comunicadoResponse> getComunicadoById(@PathVariable Long id) {
        return comunicadoService.buscarComunicadoPorId(id)
                .map(comunicado -> new ResponseEntity<>(comunicado, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @PutMapping("/{id}")
    public ResponseEntity<comunicadoResponse> atualizarComunicado(@PathVariable Long id, @RequestBody comunicadoRequest dto) {
        try {
            comunicadoResponse comunicadoAtualizado = comunicadoService.atualizarComunicado(id, dto);
            return new ResponseEntity<>(comunicadoAtualizado, HttpStatus.OK);
        } catch (SecurityException e) { // Captura a exceção de permissão negada
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // Retorna 403 Forbidden
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



    @PutMapping("/ocultar/{id}")
    public ResponseEntity<Void> ocultarComunicado(@PathVariable Long id) {
        try {
            comunicadoService.ocultarComunicadoParaUsuario(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden para remetentes tentando ocultar
        } catch (RuntimeException e) {
            // Pode ser NOT_FOUND se o comunicado não existir ou BAD_REQUEST se já estiver oculto
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @PreAuthorize("hasAnyRole('COORDENADOR', 'SUPERVISOR', 'TECNICO')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarComunicadoPermanentemente(@PathVariable Long id) { // Renomeado o método para clareza
        try {
            comunicadoService.deletarComunicadoPermanentemente(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}