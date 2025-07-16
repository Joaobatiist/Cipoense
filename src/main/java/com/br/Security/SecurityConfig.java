package com.br.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    // Removido: private final JwtAuthenticationFilter jwtAuthenticationFilter;
    // O JwtAuthenticationFilter será injetado diretamente no método securityFilterChain

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
        // Removido: this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desabilita CSRF para APIs REST (se apropriado para seu caso de uso)
                .authorizeHttpRequests(authorize -> authorize
                        // 1. Endpoints públicos (não requerem autenticação)
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        // **Caminhos do Swagger UI (SpringDoc OpenAPI)**
                        .requestMatchers(
                                "/v3/api-docs/**", // Endpoint para a especificação OpenAPI (JSON/YAML)
                                "/swagger-ui/**",  // Caminho base para os recursos estáticos do Swagger UI
                                "/swagger-ui.html", // Redirecionamento comum para a UI principal
                                "/webjars/**"      // Recursos estáticos do Swagger UI (CSS, JS, imagens)
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/atletas/nomes").permitAll() // Exemplo de endpoint público

                        // 2. Endpoints com restrições de ROLE ou autenticação
                        .requestMatchers(HttpMethod.GET,"/api/atleta/profile/**").hasRole("ATLETA")
                        .requestMatchers(HttpMethod.POST,"/api/atleta/documents").hasRole("ATLETA")
                        .requestMatchers(HttpMethod.DELETE,"/api/atleta/documents/{documentId}").hasRole("ATLETA")
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/estoque"
                        ).hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/estoque", // GET all
                                "/api/estoque/{id}" // GET by ID
                        ).hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/estoque/{id}"
                        ).hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/estoque/{id}"
                        ).hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")

                        // Endpoints de USUÁRIOS PARA COMUNICADO
                        .requestMatchers(HttpMethod.GET, "/api/usuarios-para-comunicado").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")

                        // Endpoints de PRESENÇA
                        .requestMatchers(HttpMethod.POST, "/api/presenca/registrar").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.GET, "/api/presenca/atletas").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.GET, "/api/presenca/presentes").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.GET, "/api/presenca/historico").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")

                        // Endpoints de COMUNICADOS (Criar, Atualizar, Deletar exigem roles específicas)
                        .requestMatchers(HttpMethod.POST, "/api/comunicados").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.PUT, "/api/comunicados/{id}").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.DELETE, "/api/comunicados/{id}").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")

                        // Endpoints de CADASTRO
                        .requestMatchers(HttpMethod.POST, "/api/cadastro").hasAnyRole("SUPERVISOR", "COORDENADOR") // Para cadastrar Alunos/Responsáveis
                        .requestMatchers(HttpMethod.POST, "/cadastro/funcionarios").permitAll() // Para cadastrar outros funcionários

                        // Acesso à lista de Alunos (geral)
                        .requestMatchers(HttpMethod.GET, "/api/atletas").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")

                        // Endpoints de EVENTOS (Criar, Atualizar, Deletar exigem roles específicas)
                        .requestMatchers(HttpMethod.POST, "/api/eventos").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.PUT, "/api/eventos/**").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.DELETE, "/api/eventos/**").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")


                        // Endpoints que exigem APENAS AUTENTICAÇÃO (qualquer usuário logado)
                        .requestMatchers(HttpMethod.GET, "/api/comunicados").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/comunicados/{id}").authenticated()

                        // Endpoints de ROLE ESPECÍFICA
                        .requestMatchers("/api/supervisor/**").hasRole("SUPERVISOR")
                        .requestMatchers("/api/coordenador/**").hasRole("COORDENADOR")
                        .requestMatchers("/api/tecnico/**").hasRole("TECNICO")
                        .requestMatchers("/api/atleta/**").hasRole("ATLETA") // Se houver endpoints específicos para alunos

                        // Endpoints de Listagem
                        .requestMatchers(HttpMethod.GET,"/api/atletas/listagem").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.GET,"/api/atletas/subdivisoes").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")

                        // Endpoints de Relatórios
                        .requestMatchers(HttpMethod.POST, "/api/relatoriogeral/cadastrar").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.PUT, "/api/relatoriogeral/atualizar").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.DELETE, "/api/relatoriogeral/deletar").hasAnyRole("SUPERVISOR", "COORDENADOR")
                        .requestMatchers(HttpMethod.GET, "/api/relatoriogeral/visualizar").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO", "ATLETA")

                        .requestMatchers(HttpMethod.POST, "/api/relatorios/tatico").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.PUT, "/api/relatorios/tatico/atualizar").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.DELETE, "/api/relatorios/tatico/deletar").hasAnyRole("SUPERVISOR", "COORDENADOR")
                        .requestMatchers(HttpMethod.GET, "/api/relatorios/tatico/visualizar").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO", "ATLETA")

                        .requestMatchers(HttpMethod.POST, "/api/relatorios/desempenho").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.PUT, "/api/relatorios/desempenho/atualizar").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.DELETE, "/api/relatorios/desempenho/deletar").hasAnyRole("SUPERVISOR", "COORDENADOR")
                        .requestMatchers(HttpMethod.GET, "/api/relatorios/desempenho/visualizar").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO", "ATLETA")

                        // Todas as outras requisições (catch-all) requerem autenticação
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Sem estado para JWT
                )
                // Adiciona o filtro JWT ANTES do UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}