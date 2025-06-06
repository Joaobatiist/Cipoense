package com.br.Controller;

import com.br.Response.ComunicadoResponse;
import com.br.Request.ComunicadoRequest;
import com.br.Service.ComunicadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Importe este
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comunicados")
public class ComunicadoController {

    @Autowired
    private ComunicadoService comunicadoService;

    @PreAuthorize("hasAnyRole('COORDENADOR', 'SUPERVISOR', 'TECNICO')") // Apenas criadores podem enviar
    @PostMapping
    public ResponseEntity<ComunicadoResponse> criarComunicado(@RequestBody ComunicadoRequest dto) {
        ComunicadoResponse novoComunicado = comunicadoService.criarComunicado(dto);
        return new ResponseEntity<>(novoComunicado, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ALUNO', 'COORDENADOR', 'SUPERVISOR', 'TECNICO', 'ADMIN')") // Todos podem listar
    @GetMapping
    public ResponseEntity<List<ComunicadoResponse>> getAllComunicados() {
        List<ComunicadoResponse> comunicados = comunicadoService.buscarTodosComunicados();
        return new ResponseEntity<>(comunicados, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ALUNO', 'COORDENADOR', 'SUPERVISOR', 'TECNICO', 'ADMIN')") // Todos podem buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<ComunicadoResponse> getComunicadoById(@PathVariable Long id) {
        return comunicadoService.buscarComunicadoPorId(id)
                .map(comunicado -> new ResponseEntity<>(comunicado, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAnyRole('COORDENADOR', 'SUPERVISOR', 'TECNICO')") // Apenas criadores podem atualizar (verificado no service)
    @PutMapping("/{id}")
    public ResponseEntity<ComunicadoResponse> atualizarComunicado(@PathVariable Long id, @RequestBody ComunicadoRequest dto) {
        try {
            ComunicadoResponse comunicadoAtualizado = comunicadoService.atualizarComunicado(id, dto);
            return new ResponseEntity<>(comunicadoAtualizado, HttpStatus.OK);
        } catch (SecurityException e) { // Captura a exceção de permissão negada
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // Retorna 403 Forbidden
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // **NOVO ENDPOINT: Ocultar comunicado da sessão do usuário**
    @PreAuthorize("hasAnyRole('ALUNO', 'COORDENADOR', 'SUPERVISOR', 'TECNICO')") // Destinatários (e criadores se forem destinatários)
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

    // **MÉTODO MODIFICADO: Deletar comunicado PERMANENTEMENTE**
    // Restringido para remetentes ou ADMIN (validação mais fina no Service)
    @PreAuthorize("hasAnyRole('COORDENADOR', 'SUPERVISOR', 'TECNICO', 'ADMIN')")
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