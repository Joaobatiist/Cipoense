package com.br.Controller;

import com.br.Entity.AnaliseIa;
import com.br.Repository.AnaliseIaRepository;
import com.br.Security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/atleta")
public class AtletaController {

    private final AnaliseIaRepository analiseIaRepository;

    public AtletaController(AnaliseIaRepository analiseIaRepository) {
        this.analiseIaRepository = analiseIaRepository;
    }

    @GetMapping("/minha-analise")
    public ResponseEntity<Map<String, Object>> getMinhaAnalise() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Não autenticado."));
        }

        String atletaEmail;

        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            atletaEmail = userDetails.getEmail();
        } else {
            return ResponseEntity.status(500).body(Map.of("error", "Erro interno ao processar credenciais do usuário."));
        }

        // Busca a lista de análises para o atleta, ordenada pela data decrescente.
        List<AnaliseIa> analises = analiseIaRepository.findByAtletaEmailOrderByDataAnaliseDesc(atletaEmail);

        Map<String, Object> response = new HashMap<>();
        response.put("atletaEmail", atletaEmail);
        response.put("nomeAtleta", authentication.getName()); // Usando o nome da autenticação para simplificar.

        // Verifica se a lista de análises não está vazia.
        if (!analises.isEmpty()) {
            // Pega a análise mais recente (a primeira da lista).
            AnaliseIa ultimaAnalise = analises.get(0);
            response.put("analiseDesempenhoIA", ultimaAnalise.getRespostaIA());
            response.put("dataAnalise", ultimaAnalise.getDataAnalise());
        } else {
            response.put("analiseDesempenhoIA", "Nenhuma análise de desempenho detalhada disponível no momento. Converse com seu treinador para iniciar.");
        }

        return ResponseEntity.ok(response);
    }
}