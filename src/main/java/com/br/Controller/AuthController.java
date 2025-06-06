package com.br.Controller;

import com.br.Request.AuthRequest;
import com.br.Response.AuthResponse;
import com.br.Security.JwtUtil;
import com.br.Security.UserDetailsServiceImpl;
import com.br.Repository.SupervisorRepository; // Importe seus repositórios
import com.br.Repository.CoordenadorRepository;
import com.br.Repository.TecnicoRepository;
import com.br.Repository.AlunoRepository;
import com.br.Entity.Super; // Entidade base para Supervisor, Coordenador, Tecnico
import com.br.Entity.Aluno;
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
@CrossOrigin("http://192.168.0.10:8081")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    // Injetar os repositórios para buscar o ID e o tipo após a autenticação
    private final SupervisorRepository supervisorRepository;
    private final CoordenadorRepository coordenadorRepository;
    private final TecnicoRepository tecnicoRepository;
    private final AlunoRepository alunoRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UserDetailsServiceImpl userDetailsService,
                          SupervisorRepository supervisorRepository,
                          CoordenadorRepository coordenadorRepository,
                          TecnicoRepository tecnicoRepository,
                          AlunoRepository alunoRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.supervisorRepository = supervisorRepository;
        this.coordenadorRepository = coordenadorRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.alunoRepository = alunoRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody AuthRequest authRequest) {
        try {
            // 1. Autentica o usuário. Isso verifica as credenciais (email/senha)
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getSenha()));

            // 2. Carrega UserDetails (já vem do seu UserDetailsServiceImpl)
            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());

            // 3. AGORA: Buscamos o ID e o Tipo do usuário autenticado a partir dos repositórios
            Long userId = null;
            String userType = null;

            Optional<? extends Super> foundSuperUser = Stream.of(
                            supervisorRepository.findByEmail(authRequest.getEmail()).map(s -> { // .map para garantir que o tipo seja "SUPERVISOR"
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
                userType = user.getUserType(); // Supondo que você adicione setUserType e getUserType em Super
            } else {
                Optional<Aluno> foundAluno = alunoRepository.findByEmail(authRequest.getEmail());
                if (foundAluno.isPresent()) {
                    Aluno aluno = foundAluno.get();
                    userId = aluno.getId();
                    userType = "ALUNO"; // Define o tipo para Aluno
                }
            }

            // Garante que um ID e Tipo foram encontrados
            if (userId == null || userType == null) {
                // Isso não deve acontecer se a autenticação foi bem-sucedida, mas é uma segurança
                throw new IllegalStateException("ID do usuário ou tipo não encontrado após autenticação.");
            }

            // 4. Gera o JWT, passando o ID e o Tipo para o JwtUtil
            String jwt = jwtUtil.generateToken(userDetails, userId, userType);

            // 5. Retorna a resposta com o JWT
            return ResponseEntity.ok(new AuthResponse(jwt));

        } catch (AuthenticationException e) {
            // Trata falha na autenticação (senha incorreta, usuário não encontrado, etc.)
            return ResponseEntity.status(401).body(new AuthResponse("Credenciais inválidas."));
        } catch (Exception e) {
            // Trata outros erros inesperados
            // e.printStackTrace(); // Para depuração, remova em produção
            return ResponseEntity.status(500).body(new AuthResponse("Erro interno do servidor: " + e.getMessage()));
        }
    }
}