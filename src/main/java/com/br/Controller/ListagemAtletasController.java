package com.br.Controller;

import com.br.Entity.Atleta;
import com.br.Repository.AtletaRepository;
import com.br.Response.AtletaListagemResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/atletas") // Prefixo para endpoints relacionados a atletas
public class ListagemAtletasController {

    private final AtletaRepository atletaRepository; // Injeta o repositório de Atleta

    @Autowired
    public ListagemAtletasController(AtletaRepository atletaRepository) {
        this.atletaRepository = atletaRepository;
    }

    @GetMapping("/listagem")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'COORDENADOR', 'TECNICO')") // Apenas usuários com estas roles podem acessar
    public ResponseEntity<List<AtletaListagemResponse>> listarAtletasParaSelecao() {
        // Busca todos os atletas no banco de dados
        List<Atleta> atletas = atletaRepository.findAll();
        List<AtletaListagemResponse> atletasResponse = atletas.stream()
                .map(atleta -> new AtletaListagemResponse(atleta.getId(), atleta.getNome(), atleta.getSubDivisao().toString()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(atletasResponse);
    }
    @GetMapping("/subdivisoes")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'COORDENADOR', 'TECNICO')") // Ajuste as roles conforme necessário
    public ResponseEntity<List<String>> listarSubdivisoesDistintas() {
        List<Atleta> atletas = atletaRepository.findAll();
        // Extrai as subdivisões, filtra valores nulos/vazios, remove duplicatas e coleta em uma lista de Strings
        List<String> subdivisoesDistintas = atletas.stream()
                .map(atleta -> atleta.getSubDivisao() != null ? atleta.getSubDivisao().name() : null)
                .filter(subdivisao -> subdivisao != null && !subdivisao.isEmpty())
                .distinct() // Garante que apenas valores únicos sejam retornados
                .collect(Collectors.toList());
        return ResponseEntity.ok(subdivisoesDistintas);
    }
}
