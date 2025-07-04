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

    private final com.br.Security.UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(com.br.Security.UserDetailsServiceImpl userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desabilita CSRF para APIs REST
                .authorizeHttpRequests(authorize -> authorize
                        // 1. Public endpoints (no authentication required)
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/atletas/nomes").permitAll() // Exemplo de endpoint público

                        // 2. Endpoints que exigem ROLES ESPECÍFICAS
                        // Endpoints de ESTOQUE - Corrigido e Consolidado
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
                        .requestMatchers(HttpMethod.POST, "/cadastro/funcionarios").hasAnyRole("SUPERVISOR", "COORDENADOR") // Para cadastrar outros funcionários

                        // Acesso à lista de Alunos (geral)
                        .requestMatchers(HttpMethod.GET, "/api/atletas").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")

                        // Endpoints de EVENTOS (Criar, Atualizar, Deletar exigem roles específicas - CORRIGIDO /api/eventos/**)
                        .requestMatchers(HttpMethod.POST, "/api/eventos").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.PUT, "/api/eventos/**").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.DELETE, "/api/eventos/**").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")


                        // 3. Endpoints que exigem APENAS AUTENTICAÇÃO (qualquer usuário logado)
                        // A LÓGICA DE FILTRAGEM (apenas os comunicados do usuário logado) ESTÁ NO ComunicadoService
                        .requestMatchers(HttpMethod.GET, "/api/comunicados").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/comunicados/{id}").authenticated()

                        // Endpoints de ROLE SPECÍFICA - Mantidos
                        .requestMatchers("/api/supervisor/**").hasRole("SUPERVISOR")
                        .requestMatchers("/api/coordenador/**").hasRole("COORDENADOR")
                        .requestMatchers("/api/tecnico/**").hasRole("TECNICO")
                        .requestMatchers("/api/atleta/**").hasRole("ATLETA") // Se houver endpoints específicos para alunos


                        // 4. Todas as outras requisições (catch-all) requerem autenticação
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Sem estado para JWT
                )
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