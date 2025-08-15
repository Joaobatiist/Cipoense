package com.br.Controller;

import com.br.Request.AuthRequest;
import com.br.Response.AuthResponse;
import com.br.Security.JwtUtil;
import com.br.Security.UserDetailsServiceImpl;
import com.br.Repository.SupervisorRepository; // Importe seus repositórios
import com.br.Repository.CoordenadorRepository;
import com.br.Repository.TecnicoRepository;
import com.br.Repository.AtletaRepository;
import com.br.Entity.Super; // Entidade base para Supervisor, Coordenador, Tecnico
import com.br.Entity.Atleta;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.AuthenticationException; // Importar para try-catch
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    // Injetar os repositórios para buscar o ID e o tipo após a autenticação
    private final SupervisorRepository supervisorRepository;
    private final CoordenadorRepository coordenadorRepository;
    private final TecnicoRepository tecnicoRepository;
    private final AtletaRepository atletaRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UserDetailsServiceImpl userDetailsService,
                          SupervisorRepository supervisorRepository,
                          CoordenadorRepository coordenadorRepository,
                          TecnicoRepository tecnicoRepository,
                          AtletaRepository atletaRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.supervisorRepository = supervisorRepository;
        this.coordenadorRepository = coordenadorRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.atletaRepository = atletaRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody AuthRequest authRequest) {
        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getSenha()));


            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());


            Long userId = null;
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
                Optional<Atleta> foundAtleta = atletaRepository.findByEmail(authRequest.getEmail());
                if (foundAtleta.isPresent()) {
                    Atleta atleta = foundAtleta.get();
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

            return ResponseEntity.ok(new AuthResponse(jwt));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(new AuthResponse("Credenciais inválidas."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new AuthResponse("Erro interno do servidor: " + e.getMessage()));
        }
    }
}