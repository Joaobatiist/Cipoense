package com.br.Controller;

import com.br.Entity.atleta;
import com.br.Repository.atletaRepository;
import com.br.Response.atletaListagemResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/atletas") // Prefixo para endpoints relacionados a atletas
public class listagemAtletasController {

    private final atletaRepository atletaRepository; // Injeta o repositório de Atleta

    @Autowired
    public listagemAtletasController(atletaRepository atletaRepository) {
        this.atletaRepository = atletaRepository;
    }

    @GetMapping("/listagem")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'COORDENADOR', 'TECNICO')") // Apenas usuários com estas roles podem acessar
    public ResponseEntity<List<atletaListagemResponse>> listarAtletasParaSelecao() {
        // Busca todos os atletas no banco de dados
        List<atleta> atletas = atletaRepository.findAll();
        List<atletaListagemResponse> atletasResponse = atletas.stream()
                .map(atleta ->new atletaListagemResponse(atleta.getId(), atleta.getNome(), atleta.getSubDivisao().toString(),
                        atleta.getPosicao(), atleta.getEmail()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(atletasResponse);
    }
    @GetMapping("/subdivisoes")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'COORDENADOR', 'TECNICO')") // Ajuste as roles conforme necessário
    public ResponseEntity<List<String>> listarSubdivisoesDistintas() {
        List<atleta> atletas = atletaRepository.findAll();
        List<String> subdivisoesDistintas = atletas.stream()
                .map(atleta -> atleta.getSubDivisao() != null ? atleta.getSubDivisao().name() : null)
                .filter(subdivisao -> subdivisao != null && !subdivisao.isEmpty())
                .distinct() // Garante que apenas valores únicos sejam retornados
                .collect(Collectors.toList());
        return ResponseEntity.ok(subdivisoesDistintas);
    }
    @GetMapping("/posicoes")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'COORDENADOR', 'TECNICO')")
    public ResponseEntity<List<String>> listarPosicoesDistintas() {
        List<atleta> atletas = atletaRepository.findAll();
        List<String> posicoesDistintas = atletas.stream()
                .map(atleta -> atleta.getPosicao() != null ? atleta.getPosicao().name() : null)
                .filter(posicao -> posicao != null && !posicao.isEmpty())
                .distinct() // Garante que apenas valores únicos sejam retornados
                .collect(Collectors.toList());
        return ResponseEntity.ok(posicoesDistintas);
    }

}
