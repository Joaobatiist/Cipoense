package com.br.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // <--- ESTE IMPORT É NECESSÁRIO
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class securityConfig {

    // ✅ MELHORIA: Injetar o filtro JWT aqui, como as outras dependências.
    private final userDetailsServiceImpl userDetailsService;
    private final jwtAuthenticationFilter jwtAuthenticationFilter;

    public securityConfig(userDetailsServiceImpl userDetailsService, jwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Habilita o CORS com nossa configuração
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize


                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()


                        // =================================================================
                        // 1. ENDPOINTS PÚBLICOS (Não precisam de token)
                        // =================================================================
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/atletas/nomes").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // =================================================================
                        // 2. REGRAS ESPECÍFICAS POR ROLE (Do mais específico para o mais genérico)
                        // =================================================================

                        // --- ATLETA ---
                        .requestMatchers(HttpMethod.GET, "/api/atleta/minha-analise").hasAuthority("ATLETA")
                        .requestMatchers(HttpMethod.GET,"/api/atleta/profile/**").hasAuthority("ATLETA")
                        .requestMatchers(HttpMethod.POST, "/api/atleta/profile/photo" ).hasAuthority("ATLETA")
                        .requestMatchers(HttpMethod.POST,"/api/atleta/documents").hasAuthority("ATLETA")
                        .requestMatchers(HttpMethod.DELETE,"/api/atleta/documents/**").hasAuthority("ATLETA")
                        // Catch-all para atleta (deve vir por último nas regras de atleta)
                        .requestMatchers("/api/atleta/**").hasAuthority("ATLETA")

                        // Documentos Funcionarios
                        .requestMatchers(HttpMethod.POST, "/api/cadastrar").hasAnyAuthority("SUPERVISOR", "COORDENADOR")
                        .requestMatchers(HttpMethod.GET,"/api/buscar").hasAnyAuthority("SUPERVISOR", "COORDENADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/atualizar").hasAnyAuthority("SUPERVISOR", "COORDENADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/apagar/**").hasAnyAuthority("SUPERVISOR", "COORDENADOR")
                        .requestMatchers(HttpMethod.GET, "/api/documento/download/**").hasAnyAuthority("SUPERVISOR", "COORDENADOR")

                        // --- SUPERVISOR, COORDENADOR, TÉCNICO ---
                        .requestMatchers(HttpMethod.GET, "/api/analises/atleta/**").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.DELETE, "/api/analises/delete").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.PUT, "/api/analises/Atualizar/**").hasAnyAuthority("SUPERVISOR", "COORDENADOR")
                        .requestMatchers(HttpMethod.POST, "/cadastro/funcionarios").hasAnyAuthority("SUPERVISOR", "COORDENADOR")
                        .requestMatchers(HttpMethod.DELETE,"/api/supervisor/atletas/deletar/**").hasAnyAuthority("SUPERVISOR", "COORDENADOR")
                        .requestMatchers(HttpMethod.PUT,"/api/supervisor/atletas/**").hasAnyAuthority("SUPERVISOR", "COORDENADOR")
                        .requestMatchers(HttpMethod.GET,"/api/supervisor/atletas/**").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.DELETE,"/api/supervisor/atletas/*/documento-pdf").hasAuthority("COORDENADOR")
                        .requestMatchers(HttpMethod.POST, "/api/supervisor/atletas/*/documento-pdf").hasAnyAuthority("SUPERVISOR", "COORDENADOR")
                        .requestMatchers("/api/estoque/**").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers("/api/usuarios-para-comunicado").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers( "/api/presenca/**").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.GET, "api/presenca/evento/").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/comunicados", "/api/eventos", "/api/relatoriogeral/cadastrar", "/api/relatorios/tatico", "/api/relatorios/desempenho").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/comunicados/**", "/api/eventos/**", "/api/relatoriogeral/**", "/api/relatorios/tatico/atualizar", "/api/relatorios/desempenho/atualizar").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.DELETE, "/api/comunicados/**", "/api/eventos/**", "/api/relatoriogeral/deletarporid/**", "/api/relatorios/tatico/deletar", "/api/relatorios/desempenho/deletar").hasAnyAuthority("SUPERVISOR", "COORDENADOR")
                        .requestMatchers(HttpMethod.GET, "/api/por-titulo").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.POST, "/api/cadastro").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/atletas", "/api/atletas/listagem", "/api/atletas/subdivisoes").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.GET, "/api/funcionarios/listarfuncionarios").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.PUT, "/api/funcionarios/**").hasAnyAuthority("SUPERVISOR", "COORDENADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/funcionarios/**").hasAnyAuthority("SUPERVISOR", "COORDENADOR")
                        .requestMatchers(HttpMethod.GET, "/api/relatoriogeral/listar").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")

                        // Catch-all para roles de gestão
                        .requestMatchers("/api/supervisor/**").hasAuthority("SUPERVISOR")
                        .requestMatchers("/api/coordenador/**").hasAuthority("COORDENADOR")
                        .requestMatchers("/api/tecnico/**").hasAuthority("TECNICO")

                        // =================================================================
                        // 3. REGRAS PARA QUALQUER USUÁRIO AUTENTICADO
                        // =================================================================
                        .requestMatchers(HttpMethod.GET, "/api/eventos/**", "/api/comunicados/**", "/api/comunicados/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/relatoriogeral/visualizar/**", "/api/relatoriogeral/buscarporid/**", "/api/relatorios/tatico/visualizar", "/api/relatorios/desempenho/visualizar").authenticated()

                        // =================================================================
                        // 4. REGRA FINAL (Catch-All)
                        // =================================================================
                        .anyRequest().authenticated()
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // ⭐️ CORREÇÃO: Adicione a URL do seu front-end de produção!
        configuration.setAllowedOrigins(Arrays.asList(
                "https://adcipeonse.cloud",
                "http://localhost:8081",
                "http://192.1.4:8080",// Pode manter para testes na rede local
                "http://localhost:8080"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*")); // Permite todos os cabeçalhos
        configuration.setAllowCredentials(true); // Essencial para cookies/autenticação

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica a configuração a todos os paths
        return source;
    }
}