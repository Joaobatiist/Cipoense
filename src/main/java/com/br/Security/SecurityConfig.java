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
                        // Public endpoints (no authentication required)
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/usuarios-para-comunicado").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.GET, "/api/alunos/nomes").permitAll() // Se esta rota não exige autenticação

                        // Endpoints de Eventos: Qualquer usuário autenticado pode ver (a filtragem de conteúdo, se houver, deve ser no serviço)
                        .requestMatchers(HttpMethod.GET, "/api/eventos").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/eventos/{id}").authenticated()

                        // Endpoints de Comunicados: Qualquer usuário autenticado pode ver a lista de comunicados
                        // A LÓGICA DE FILTRAGEM (apenas os comunicados do usuário logado) ESTÁ NO ComunicadoService
                        .requestMatchers(HttpMethod.GET, "/api/comunicados").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/comunicados/{id}").authenticated()


                        // Apenas usuários com certas roles podem CRIAR, ATUALIZAR, DELETAR comunicados
                        .requestMatchers(HttpMethod.POST, "/api/comunicados").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.PUT, "/api/comunicados/{id}").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.DELETE, "/api/comunicados/{id}").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")

                        // Endpoints de Cadastro
                        .requestMatchers(HttpMethod.POST, "/api/cadastro").hasAnyRole("SUPERVISOR", "COORDENADOR") // Para cadastrar Alunos/Responsáveis
                        .requestMatchers(HttpMethod.POST, "/cadastro/funcionarios").hasAnyRole("SUPERVISOR", "COORDENADOR") // Para cadastrar outros funcionários

                        // Acesso à lista de Alunos (geral)
                        .requestMatchers(HttpMethod.GET, "/api/alunos").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")


                        // Apenas usuários com certas roles podem CRIAR, ATUALIZAR, DELETAR eventos
                        .requestMatchers(HttpMethod.POST, "/api/eventos").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.PUT, "/api/eventos/**").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.DELETE, "/api/eventos/**").hasAnyRole("SUPERVISOR", "COORDENADOR", "TECNICO")

                        // Acesso a dashboards/seções baseados em role (seus próprios recursos ou informações específicas)
                        // É uma boa prática usar "/**" para cobrir sub-recursos.
                        .requestMatchers("/api/supervisor/**").hasRole("SUPERVISOR")
                        .requestMatchers("/api/coordenador/**").hasRole("COORDENADOR")
                        .requestMatchers("/api/tecnico/**").hasRole("TECNICO")
                        .requestMatchers("/api/aluno/**").hasRole("ALUNO") // Se houver endpoints específicos para alunos

                        // Todas as outras requisições requerem autenticação (regra genérica final)
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