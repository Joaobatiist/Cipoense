package com.br.Controller;

import com.br.Request.authRequest;
import com.br.Response.authResponse;
import com.br.Security.jwtUtil;
import com.br.Security.userDetailsServiceImpl;
import com.br.Repository.funcionarioRepository; // Novo repositório unificado
import com.br.Repository.atletaRepository;

import com.br.Entity.funcionario;
import com.br.Entity.atleta;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class authController {

    private final AuthenticationManager authenticationManager;
    private final jwtUtil jwtUtil;
    private final userDetailsServiceImpl userDetailsService;

    // Injetar apenas os repositórios unificados e de Atleta
    private final funcionarioRepository funcionarioRepository;
    private final atletaRepository atletaRepository;

    public authController(AuthenticationManager authenticationManager,
                          jwtUtil jwtUtil,
                          userDetailsServiceImpl userDetailsService,
                          funcionarioRepository funcionarioRepository, // Repositório unificado
                          atletaRepository atletaRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.funcionarioRepository = funcionarioRepository;
        this.atletaRepository = atletaRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<authResponse> authenticateUser(@RequestBody authRequest authRequest) {
        try {
            // 1. AUTENTICAÇÃO (Spring Security verifica credenciais)
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getSenha()));

            // 2. CARREGAMENTO DOS DETALHES (O Spring Security usa o UserDetails obtido aqui)
            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());

            UUID userId = null;
            String userType = null;
            String entityName = null;

            // 3. BUSCA UNIFICADA POR FUNCIONÁRIO (Substitui o Stream.of() complexo)
            Optional<funcionario> foundFuncionario = funcionarioRepository.findByEmail(authRequest.getEmail());

            if (foundFuncionario.isPresent()) {
                funcionario user = foundFuncionario.get();
                userId = user.getId();
                // A Role é extraída diretamente da entidade Funcionario
                userType = user.getRole().name(); // Assumindo que getRoles() retorna o Enum (SUPERVISOR, COORDENADOR, TECNICO)
                entityName = user.getNome();
            } else {
                // 4. BUSCA POR ATLETA
                Optional<atleta> foundAtleta = atletaRepository.findByEmail(authRequest.getEmail());
                if (foundAtleta.isPresent()) {
                    atleta atleta = foundAtleta.get();
                    userId = atleta.getId();
                    userType = "ATLETA";
                    // boolean userIsencao = atleta.isIsencao(); // Esta variável não era usada, mas pode ser mantida
                    entityName = atleta.getNome();
                }
            }

            // 5. VALIDAÇÃO E GERAÇÃO DO TOKEN
            if (userId == null || userType == null || entityName == null) {
                // Se o usuário foi autenticado, mas os dados de ID/Tipo não foram encontrados, é um erro lógico.
                throw new IllegalStateException("Dados do usuário (ID, tipo ou nome) não encontrados após autenticação.");
            }

            String jwt = jwtUtil.generateToken(userDetails, userId, userType, entityName);

            return ResponseEntity.ok(new authResponse(jwt));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(new authResponse("Credenciais inválidas."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new authResponse("Erro interno do servidor: " + e.getMessage()));
        }
    }
}