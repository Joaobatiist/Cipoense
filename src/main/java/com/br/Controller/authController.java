package com.br.Controller;

import com.br.Request.authRequest;
import com.br.Response.authResponse;
import com.br.Security.jwtUtil;
import com.br.Security.userDetailsServiceImpl;
import com.br.Repository.supervisorRepository; // Importe seus repositórios
import com.br.Repository.coordenadorRepository;
import com.br.Repository.tecnicoRepository;
import com.br.Repository.atletaRepository;
import com.br.Entity.Super; // Entidade base para Supervisor, Coordenador, Tecnico
import com.br.Entity.atleta;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.AuthenticationException; // Importar para try-catch
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@RestController
@RequestMapping("/auth")
public class authController {

    private final AuthenticationManager authenticationManager;
    private final jwtUtil jwtUtil;
    private final userDetailsServiceImpl userDetailsService;

    // Injetar os repositórios para buscar o ID e o tipo após a autenticação
    private final supervisorRepository supervisorRepository;
    private final coordenadorRepository coordenadorRepository;
    private final tecnicoRepository tecnicoRepository;
    private final atletaRepository atletaRepository;

    public authController(AuthenticationManager authenticationManager,
                          jwtUtil jwtUtil,
                          userDetailsServiceImpl userDetailsService,
                          supervisorRepository supervisorRepository,
                          coordenadorRepository coordenadorRepository,
                          tecnicoRepository tecnicoRepository,
                          atletaRepository atletaRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.supervisorRepository = supervisorRepository;
        this.coordenadorRepository = coordenadorRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.atletaRepository = atletaRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<authResponse> authenticateUser(@RequestBody authRequest authRequest) {
        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getSenha()));


            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());


            UUID userId = null;
            String userType = null;
            String entityName = null;


            Optional<? extends Super> foundSuperUser = Stream.of(
                            supervisorRepository.findByEmail(authRequest.getEmail()).map(s -> {
                                s.setUserType("SUPERVISOR");
                                return s;
                            }),
                            coordenadorRepository.findByEmail(authRequest.getEmail()).map(c -> {
                                c.setUserType("COORDENADOR");
                                return c;
                            }),
                            tecnicoRepository.findByEmail(authRequest.getEmail()).map(t -> {
                                t.setUserType("TECNICO");
                                return t;
                            })
                    )
                    .flatMap(Optional::stream)
                    .findFirst();

            if (foundSuperUser.isPresent()) {
                Super user = foundSuperUser.get();
                userId = user.getId();
                userType = user.getUserType();
                entityName = user.getNome();
            } else {
                Optional<atleta> foundAtleta = atletaRepository.findByEmail(authRequest.getEmail());
                if (foundAtleta.isPresent()) {
                    atleta atleta = foundAtleta.get();
                    userId = atleta.getId();
                    userType = "ATLETA";
                    boolean userIsencao = atleta.isIsencao();
                    entityName = atleta.getNome();
                }
            }


            if (userId == null || userType == null || entityName == null) {
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