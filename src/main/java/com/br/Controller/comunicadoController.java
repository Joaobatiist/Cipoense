package com.br.Controller;

import com.br.Response.comunicadoResponse;
import com.br.Request.comunicadoRequest;
import com.br.Service.comunicadoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comunicados")
public class comunicadoController {

    private final comunicadoService comunicadoService;

    public comunicadoController(comunicadoService comunicadoService) {
        this.comunicadoService = comunicadoService;
    }

    // ⭐ SEGURANÇA: Apenas usuários com esses papéis podem criar comunicados
    @PostMapping
    @PreAuthorize("hasAnyRole('COORDENADOR', 'SUPERVISOR', 'TECNICO')")
    public ResponseEntity<comunicadoResponse> criarComunicado(@RequestBody comunicadoRequest dto) {
        comunicadoResponse novoComunicado = comunicadoService.criarComunicado(dto);
        return new ResponseEntity<>(novoComunicado, HttpStatus.CREATED);
    }

    // Todos os usuários autenticados podem listar. O service refatorado já remove as duplicatas.
    @GetMapping
    public ResponseEntity<List<comunicadoResponse>> getAllComunicados() {
        List<comunicadoResponse> comunicados = comunicadoService.buscarTodosComunicados();
        return new ResponseEntity<>(comunicados, HttpStatus.OK);
    }

    // ⭐ CORREÇÃO: Método corrigido para chamar o serviço certo (`buscarComunicadoPorId`)
    @GetMapping("/{id}")
    public ResponseEntity<comunicadoResponse> getComunicadoById(@PathVariable UUID id) {
        return comunicadoService.buscarComunicadoPorId(id)
                .map(comunicado -> new ResponseEntity<>(comunicado, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // ⭐ SEGURANÇA: Apenas usuários com esses papéis podem atualizar comunicados
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'SUPERVISOR', 'TECNICO')")
    public ResponseEntity<comunicadoResponse> atualizarComunicado(@PathVariable UUID id, @RequestBody comunicadoRequest dto) {
        try {
            comunicadoResponse comunicadoAtualizado = comunicadoService.atualizarComunicado(id, dto);
            return new ResponseEntity<>(comunicadoAtualizado, HttpStatus.OK);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Qualquer usuário autenticado pode tentar ocultar um comunicado que recebeu
    @PutMapping("/ocultar/{id}")
    public ResponseEntity<Void> ocultarComunicado(@PathVariable UUID id) {
        try {
            comunicadoService.ocultarComunicadoParaUsuario(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Apenas os papéis especificados (ou um ADMIN) podem deletar permanentemente
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORDENADOR', 'SUPERVISOR', 'TECNICO', 'ADMIN')")
    public ResponseEntity<Void> deletarComunicadoPermanentemente(@PathVariable UUID id) {
        try {
            comunicadoService.deletarComunicadoPermanentemente(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}